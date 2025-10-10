package com.payroc.hospitaloperations.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.payroc.hospitaloperations.dto.OperationDTO;
import com.payroc.hospitaloperations.dto.PatientDTO;
import com.payroc.hospitaloperations.dto.RequestCacheResponseDTO;
import com.payroc.hospitaloperations.entity.Operation;
import com.payroc.hospitaloperations.entity.Patient;
import com.payroc.hospitaloperations.entity.RequestCache;
import com.payroc.hospitaloperations.enumeration.ExceptionEnum;
import com.payroc.hospitaloperations.enumeration.PatientStatusEnum;
import com.payroc.hospitaloperations.exception.HospitalOperationException;
import com.payroc.hospitaloperations.service.OperationService;
import com.payroc.hospitaloperations.service.PatientService;
import com.payroc.hospitaloperations.service.RequestCacheService;
import com.payroc.hospitaloperations.util.ValidationUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class PatientController {

	private final PatientService patientService;
	private final RequestCacheService requestCacheService;
	private final OperationService operationService;

	public PatientController( 
			PatientService patientService,
			RequestCacheService requestCacheService,
			OperationService operationService ) {
		this.patientService = patientService;
		this.requestCacheService = requestCacheService;
		this.operationService = operationService;

		if (patientService.getAllPatients().isEmpty()) {
			patientService.admitPatient("Steve");
			patientService.admitPatient("Ana");
			patientService.admitPatient("Jim");
			patientService.admitPatient("David");
			patientService.admitPatient("Sarah");
		}
	}

	@GetMapping("/patients")
	public List<PatientDTO> getAllPatients() {
		List<Patient> patients = patientService.getAllPatients();
		List<PatientDTO> patientDTOs = patients.stream()
	            .map(patient -> new PatientDTO(patient)).collect(Collectors.toList());
		return patientDTOs;
	}
	
	@GetMapping("/patients/{id}")
	public PatientDTO getPatientById( @PathVariable("id") final Integer patientId ) {

		ValidationUtils.validateMandatoryField(patientId, "patientId");
		Patient patient = patientService.getPatientById( patientId );
		
		if ( patient == null )
		{
			throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4040);
		}
		
		return new PatientDTO( patient );
	}
	
	 @PostMapping("/patients/{id}/discharge")
	 public ResponseEntity<Object> dischargePatient( 
		@RequestHeader("x-idempotency-key") String idempotencyKey,
	 	@PathVariable("id") final Integer patientId,
		HttpServletRequest request ) throws Exception {
		 
		ValidationUtils.validateMandatoryField(patientId, "patientId");
		
		//if null idempotency key then throw exception
		if (idempotencyKey == null) {
			throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4000);
		}
		//Generate composite key
		String requestScope = request.getMethod() + ":" + request.getRequestURI();
        String compositeKey = requestScope + ":" + idempotencyKey;

		//If already requested, return previous response.
        RequestCache previousRequest = requestCacheService.getRequestCacheByKey(compositeKey);
        if ( previousRequest != null )
        {
        	RequestCacheResponseDTO previousResponseDTO = requestCacheService.getRequestCacheResponseAsDTO(previousRequest);
			HttpStatusCode status = HttpStatusCode.valueOf( previousResponseDTO.getResponseCode() );
        	return new ResponseEntity(previousResponseDTO.getResponseBody(), status );
        }
        
        //Validate
		Patient patient = patientService.getPatientById( patientId );
		
		if ( patient == null )
		{
			throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4040);
		}
		if ( patient.getStatus().equals( PatientStatusEnum.DISCHARGED ))
		{
			throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4001);
		}
		 
		//Submit discharge operation
	 	Operation op = operationService.submitPatientDischargeOperation( compositeKey, patient );
	 	OperationDTO responseDTO = new OperationDTO(op);
	 	RequestCacheResponseDTO cacheResponseDTO = new RequestCacheResponseDTO( 202, responseDTO);
	 	requestCacheService.cacheRequest( compositeKey, cacheResponseDTO );
	 	return new ResponseEntity( responseDTO, HttpStatusCode.valueOf(202) );
	 }
	 
	 @PostMapping("/patients/{id}/discharge/undo")
	 public ResponseEntity<Object> undoDischargePatient( 
			 @RequestHeader("x-idempotency-key") String idempotencyKey,
			 @PathVariable("id") final Integer patientId,
			 HttpServletRequest request ) throws Exception {
		 
		 ValidationUtils.validateMandatoryField(patientId, "patientId");
		 
		 //if null idempotency key then throw exception
		 if (idempotencyKey == null) {
			 throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4000);
		 }
		 //Generate composite key
		 String requestScope = request.getMethod() + ":" + request.getRequestURI();
		 String compositeKey = requestScope + ":" + idempotencyKey;
		 
		 //If already requested, return previous response.
		 RequestCache previousRequest = requestCacheService.getRequestCacheByKey(compositeKey);
		 if ( previousRequest != null )
		 {
			 RequestCacheResponseDTO previousResponseDTO = requestCacheService.getRequestCacheResponseAsDTO(previousRequest);
			 HttpStatusCode status = HttpStatusCode.valueOf( previousResponseDTO.getResponseCode() );
			 return new ResponseEntity(previousResponseDTO.getResponseBody(), status );
		 }
		 
		 //Validate
		 Patient patient = patientService.getPatientById( patientId );
		 
		 if ( patient == null )
		 {
			 throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4040);
		 }
		 if ( patient.getStatus().equals( PatientStatusEnum.ADMITTED ))
		 {
			 throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4002);
		 }
		 
		 //Submit discharge operation
		 Operation op = operationService.submitUndoPatientDischargeOperation( compositeKey, patient );
		 OperationDTO responseDTO = new OperationDTO(op);
		 RequestCacheResponseDTO cacheResponseDTO = new RequestCacheResponseDTO( 202, responseDTO);
		 requestCacheService.cacheRequest( compositeKey, cacheResponseDTO );
		 return new ResponseEntity( responseDTO, HttpStatusCode.valueOf(202) );
	 }
}
