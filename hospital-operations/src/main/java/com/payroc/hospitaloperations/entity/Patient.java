package com.payroc.hospitaloperations.entity;

import java.util.Date;

import com.payroc.hospitaloperations.enumeration.PatientStatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@NamedQueries({
    @NamedQuery(
        name = "Patient.findAll",
        query = "SELECT p FROM Patient p"
    ),
    @NamedQuery(
        name = "Patient.findByName",
        query = "SELECT p FROM Patient p WHERE p.name = :name"
    ),
    @NamedQuery(
    		name = "Patient.getById",
    		query = "SELECT p FROM Patient p WHERE p.id = :id"
    		)
})
@Table(name = "patients")
public class Patient {
	
	public static String FIND_ALL = "Patient.findAll";
	public static String FIND_BY_NAME = "Patient.findByName";
	public static String GET_BY_ID = "Patient.getById";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Enumerated(EnumType.STRING)
    private PatientStatusEnum status = PatientStatusEnum.ADMITTED;
    private Date dateAdmitted;
    private Date dateDischarged;

    public Patient() {}

    public Patient(final String name) {
        this.name = name;
    }
    
    @PrePersist
    protected void onCreation()
    {
        dateAdmitted = new Date();
    }

    public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public PatientStatusEnum getStatus() {
		return status;
	}

	public void setStatus(final PatientStatusEnum status) {
		this.status = status;
	}
	
	public Date getDateAdmitted() {
		return dateAdmitted;
	}

	public void setDateAdmitted(final Date dateAdmitted) {
		this.dateAdmitted = dateAdmitted;
	}

	public Date getDateDischarged() {
		return dateDischarged;
	}

	public void setDateDischarged( final Date dateDischarged) {
		this.dateDischarged = dateDischarged;
	}

	@Override
    public String toString() {
        return id + ": " + name;
    }
}