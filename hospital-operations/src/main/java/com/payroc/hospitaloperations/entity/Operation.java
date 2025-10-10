package com.payroc.hospitaloperations.entity;

import java.util.Date;

import com.payroc.hospitaloperations.enumeration.OperationStatusEnum;
import com.payroc.hospitaloperations.enumeration.OperationTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@NamedQueries({
    @NamedQuery(
    		name = "Operation.getById",
    		query = "SELECT o FROM Operation o WHERE o.operationId = :id"
    		),
    @NamedQuery(
    		name = "Operation.getBySubmissionDateAsc",
    		query = "SELECT o FROM Operation o "
    				+ "WHERE o.operationStatus = :status "
    				+ "order by o.dateSubmitted asc"
    		),
    @NamedQuery(
    		name = "Operation.getByStatusAndDateAsc",
    		query = "SELECT o FROM Operation o "
    				+ "WHERE o.operationStatus = :status "
    				+ "order by o.dateSubmitted asc"
    		)
})
@Table(name = "operation")
public class Operation {
	
	public static String GET_BY_ID = "Operation.getById";
	public static String GET_BY_SUBMISSION_DATE_ASC = "Operation.getBySubmissionDateAsc";
	public static String GET_BY_STATUS_AND_DATE_ASCENDING = "Operation.getByStatusAndDateAsc";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer operationId;
	@Enumerated(EnumType.STRING)
	private OperationTypeEnum operationType;
	@Lob
	private String operationData;
	@Enumerated(EnumType.STRING)
	private OperationStatusEnum operationStatus;
	private Date dateSubmitted;
	private Date dateQueued;
	private Date dateProcessed;
	@Lob
	private String metadata;
	private String idempotencyKey;
	
    public Operation() {}
    
    @PrePersist
    protected void onCreation()
    {
        operationStatus = OperationStatusEnum.PENDING;
		dateQueued = new Date();
		dateSubmitted = new Date();
    }

    public Integer getOperationId() {
		return operationId;
	}

	public OperationTypeEnum getOperationType() {
		return operationType;
	}

	public void setOperationType( final OperationTypeEnum operationType) {
		this.operationType = operationType;
	}

	public String getOperationData() {
		return operationData;
	}

	public void setOperationData( final String operationData) {
		this.operationData = operationData;
	}

	public OperationStatusEnum getOperationStatus() {
		return operationStatus;
	}

	public void setOperationStatus( final OperationStatusEnum operationStatus) {
		this.operationStatus = operationStatus;
	}

	public Date getDateSubmitted() {
		return dateSubmitted;
	}

	public void setDateSubmitted( final Date dateSubmitted) {
		this.dateSubmitted = dateSubmitted;
	}

	public Date getDateQueued() {
		return dateQueued;
	}

	public void setDateQueued( final Date dateQueued) {
		this.dateQueued = dateQueued;
	}

	public Date getDateProcessed() {
		return dateProcessed;
	}

	public void setDateProcessed( final Date dateProcessed) {
		this.dateProcessed = dateProcessed;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata( final String metadata) {
		this.metadata = metadata;
	}

	

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey( final String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public void setOperationId( final Integer operationId) {
		this.operationId = operationId;
	}

	public static String getGET_BY_ID() {
		return GET_BY_ID;
	}

	public static void setGET_BY_ID(String gET_BY_ID) {
		GET_BY_ID = gET_BY_ID;
	}

	@Override
    public String toString() {
        return operationId + ": " + operationType + " - " + operationStatus;
    }
}