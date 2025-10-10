package com.payroc.hospitaloperations.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.payroc.hospitaloperations.entity.Operation;
import com.payroc.hospitaloperations.entity.Patient;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class OperationDao {

    @PersistenceContext
    private EntityManager em;

//    public List<Operation> findAll() {
//        return em.createNamedQuery(Operation.FIND_ALL, Operation.class)
//                 .getResultList();
//    }
//
    public Operation getById(Integer id) {
        try {
        return em.createNamedQuery(Operation.GET_BY_ID, Operation.class)
                 .setParameter("id", id)
                 .getSingleResult();
        } catch (Exception e) {
        	return null;
        }
    }
//    
//    public List<Patient> findPatientsByName(String name) {
//    	return em.createNamedQuery(Operation.FIND_BY_NAME, Operation.class)
//    			.setParameter("name", name)
//    			.getResultList();
//    }

    public void save(Operation operation) {
        em.persist(operation);
    }

    public void update(Operation operation) {
        em.merge( operation );
    }
}