package com.payroc.hospitaloperations.dao;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.payroc.hospitaloperations.entity.Operation;
import com.payroc.hospitaloperations.enumeration.OperationStatusEnum;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class OperationDao {

    @PersistenceContext
    private EntityManager em;

    public Operation getById(Integer id) {
        try {
        return em.createNamedQuery(Operation.GET_BY_ID, Operation.class)
                 .setParameter("id", id)
                 .getSingleResult();
        } catch (Exception e) {
        	return null;
        }
    }
    
    public Operation getOldestOperationWithStatus( final OperationStatusEnum status ) {
    	try {
    		return em.createNamedQuery(Operation.GET_BY_SUBMISSION_DATE_ASC, Operation.class)
    				.setParameter("status", status)
    				.setMaxResults(1)
    				.getSingleResult();
    	} catch (Exception e) {
    		return null;
    	}
    }
    
    public List<Operation> getOperationsWithStatus( final OperationStatusEnum status ) {
    		return em.createNamedQuery(Operation.GET_BY_STATUS_AND_DATE_ASCENDING, Operation.class)
    				.setParameter("status", status)
    				.getResultList();
    }

    public void save(Operation operation) {
        em.persist(operation);
    }

    public void update(Operation operation) {
        em.merge( operation );
    }
}