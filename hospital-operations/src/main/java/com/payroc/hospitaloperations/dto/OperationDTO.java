package com.payroc.hospitaloperations.dto;

import com.payroc.hospitaloperations.entity.Operation;

public class OperationDTO {
	
	private Integer operationId;
	
	public OperationDTO( final Operation operation ){
		this.operationId = operation.getOperationId();
	}

	public Integer getOperationId() {
		return operationId;
	}
}