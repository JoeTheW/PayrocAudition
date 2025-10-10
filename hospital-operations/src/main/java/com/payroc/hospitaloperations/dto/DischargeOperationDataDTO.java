package com.payroc.hospitaloperations.dto;


public class DischargeOperationDataDTO {
	
	private Integer patientId;
	
	public DischargeOperationDataDTO() {
		
	}
	
	public DischargeOperationDataDTO( final Integer patientId ){
		this.patientId = patientId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	
}