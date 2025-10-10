package com.payroc.hospitaloperations.entity;

import java.util.Date;

import jakarta.persistence.Entity;
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
    		name = "RequestCache.getByKey",
    		query = "SELECT rq FROM RequestCache rq WHERE rq.idempotencyKey = :key"
    		)
})
@Table(name = "request_cache")
public class RequestCache {
	
	public static String GET_BY_KEY = "RequestCache.getByKey";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestCacheId;
    private String idempotencyKey;
    @Lob
    private String response;
    private Date dateCreated;

	
    public RequestCache() {}
    
    public RequestCache( String idempotencyKey, String responseJson ) {
    	this.idempotencyKey = idempotencyKey;
    	this.response = responseJson;
    }
    
    @PrePersist
    protected void onCreation()
    {
    	dateCreated = new Date();
    }

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey( final String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse( final String response) {
		this.response = response;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated( final Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Integer getRequestCacheId() {
		return requestCacheId;
	}

	@Override
    public String toString() {
        return requestCacheId + ": " + idempotencyKey + " - " + dateCreated;
    }
}