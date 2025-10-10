package com.payroc.hospitaloperations.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.hospitaloperations.dao.OperationDao;
import com.payroc.hospitaloperations.dto.DischargeOperationDataDTO;
import com.payroc.hospitaloperations.entity.Operation;
import com.payroc.hospitaloperations.entity.Patient;
import com.payroc.hospitaloperations.enumeration.ExceptionEnum;
import com.payroc.hospitaloperations.enumeration.OperationStatusEnum;
import com.payroc.hospitaloperations.enumeration.OperationTypeEnum;
import com.payroc.hospitaloperations.exception.HospitalOperationException;

@Service
public class OperationService {
	private static final Logger logger = LoggerFactory.getLogger(OperationService.class);

    private final OperationDao operationDao;
    private final PatientService patientService;

    public OperationService( OperationDao operationDao,
    		PatientService patientService ) {
        this.operationDao = operationDao;
        this.patientService = patientService;
    }

    @Transactional(readOnly = true)
    public Operation getOperationById( Integer id ) {
        return operationDao.getById( id );
    }

    @Transactional
    public Operation submitPatientDischargeOperation(final String idempotencyKey, final Patient patient )
    throws HospitalOperationException 
    {
        Operation operation = new Operation();
        operation.setOperationType(OperationTypeEnum.DISCHARGE_PATIENT);
        operation.setIdempotencyKey( idempotencyKey );
        
        DischargeOperationDataDTO operationData = new DischargeOperationDataDTO(patient.getId());
        // Map data to json string
        ObjectMapper mapper = new ObjectMapper();
        try {
            String operationDataJson = mapper.writeValueAsString(operationData);
            operation.setOperationData( operationDataJson );
            operationDao.save(operation);
            return operation;
        } catch (Exception e) {
			// log error
			logger.error("Error parsing cached response: {}", e.getMessage(), e);
            throw new HospitalOperationException(ExceptionEnum.EXCEPTION_5001);
        }
    }
    
    @Transactional
    public Operation submitUndoPatientDischargeOperation(final String idempotencyKey, final Patient patient )
    		throws HospitalOperationException 
    {
    	Operation operation = new Operation();
    	operation.setOperationType(OperationTypeEnum.UNDO_DISCHARGE_PATIENT);
    	operation.setIdempotencyKey( idempotencyKey );
    	
    	DischargeOperationDataDTO operationData = new DischargeOperationDataDTO(patient.getId());
    	// Map data to json string
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		String operationDataJson = mapper.writeValueAsString(operationData);
    		operation.setOperationData( operationDataJson );
    		operationDao.save(operation);
    		return operation;
    	} catch (Exception e) {
    		// log error
    		logger.error("Error parsing cached response: {}", e.getMessage(), e);
    		throw new HospitalOperationException(ExceptionEnum.EXCEPTION_5001);
    	}
    }
    
    @Transactional(readOnly = true)
    public Operation getOldestOperationWithStatus( final OperationStatusEnum status )
    {
    	return operationDao.getOldestOperationWithStatus(status);
    }
    
    @Transactional(readOnly = true)
    public List<Operation> getOperationsWithStatus( final OperationStatusEnum status )
    {
    	return operationDao.getOperationsWithStatus(status);
    }

    @Transactional
    public void updateOperation( Operation operation )
    {
    	operationDao.update( operation );
    }

	public boolean performOperation(Operation operation) {
		switch ( operation.getOperationType() )
        {
            case DISCHARGE_PATIENT:
                DischargeOperationDataDTO dischargeDataDto = parseOperationData(operation, DischargeOperationDataDTO.class);
                patientService.performPatientDischargeOperation( dischargeDataDto.getPatientId() );
                return true;
            case UNDO_DISCHARGE_PATIENT:
            	DischargeOperationDataDTO undoDischargeDataDto = parseOperationData(operation, DischargeOperationDataDTO.class);
                patientService.performUndoPatientDischargeOperation( undoDischargeDataDto.getPatientId() );
                return true;
            default:
                logger.error("Unknown operation type: " + operation.getOperationType() );
                return false;
        }
	}

    public <T> T parseOperationData(Operation operation, Class<T> clazz)
    throws HospitalOperationException
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(operation.getOperationData(), clazz);
        } catch (Exception e) {
            logger.error("Failed to parse operationData for operation {}: {}", operation.getOperationId(), e.getMessage(), e);
            throw new HospitalOperationException( ExceptionEnum.EXCEPTION_5001 );
        }
    }
}