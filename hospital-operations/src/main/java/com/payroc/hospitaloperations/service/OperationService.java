package com.payroc.hospitaloperations.service;


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
import com.payroc.hospitaloperations.enumeration.OperationTypeEnum;
import com.payroc.hospitaloperations.exception.HospitalOperationException;

@Service
public class OperationService {
	private static final Logger logger = LoggerFactory.getLogger(RequestCacheService.class);

    private final OperationDao operationDao;

    public OperationService( OperationDao operationDao ) {
        this.operationDao = operationDao;
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
}