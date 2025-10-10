package com.payroc.hospitaloperations.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.payroc.hospitaloperations.dto.PatientDTO;
import com.payroc.hospitaloperations.entity.Patient;
import com.payroc.hospitaloperations.enumeration.ExceptionEnum;
import com.payroc.hospitaloperations.exception.HospitalOperationException;
import com.payroc.hospitaloperations.service.PatientService;
import com.payroc.hospitaloperations.util.ValidationUtils;

@RestController
public class PatientController {

	private final PatientService patientService;

	public PatientController( PatientService patientService ) {
		this.patientService = patientService;

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
}
