package com.payroc.hospitaloperations.dto;

import com.payroc.hospitaloperations.entity.Operation;
import com.payroc.hospitaloperations.entity.Patient;
import com.payroc.hospitaloperations.enumeration.PatientStatusEnum;

public class OperationDTO {
	
	private Integer operationId;
	
	public OperationDTO( final Operation operation ){
		this.operationId = operation.getOperationId();
	}

	public Integer getOperationId() {
		return operationId;
	}
}