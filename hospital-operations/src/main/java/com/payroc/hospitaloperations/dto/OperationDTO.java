package com.payroc.hospitaloperations.dto;

import com.payroc.hospitaloperations.entity.Operation;
import com.payroc.hospitaloperations.entity.Patient;
import com.payroc.hospitaloperations.enumeration.PatientStatusEnum;

public class OperationDTO {
	
	private Integer id;
	
	public OperationDTO( final Operation operation ){
		this.id = operation.getOperationId();
	}

	public Integer getId() {
		return id;
	}
}