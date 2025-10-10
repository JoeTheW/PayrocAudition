package com.payroc.hospitaloperations.dto;

import com.payroc.hospitaloperations.entity.Patient;
import com.payroc.hospitaloperations.enumeration.PatientStatusEnum;

public class PatientDTO {
	
	private Integer id;
	private String name;
	private PatientStatusEnum status;
	
	public PatientDTO( final Patient patient ){
		this.id = patient.getId();
		this.name = patient.getName();
		this.status = patient.getStatus();
	}

	public Integer getId() {
		return id;
	}

	public void setId( final Integer id ) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName( final String name ) {
		this.name = name;
	}

	public PatientStatusEnum getStatus() {
		return status;
	}

	public void setStatus(final PatientStatusEnum status) {
		this.status = status;
	}

}