package com.payroc.hospitaloperations.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.payroc.hospitaloperations.entity.Operation;
import com.payroc.hospitaloperations.entity.Patient;
import com.payroc.hospitaloperations.entity.RequestCache;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class RequestCacheDao {

    @PersistenceContext
    private EntityManager em;

//    public List<Operation> findAll() {
//        return em.createNamedQuery(Operation.FIND_ALL, Operation.class)
//                 .getResultList();
//    }
//
    public RequestCache getByKey(String key) {
        try {
        return em.createNamedQuery(RequestCache.GET_BY_KEY, RequestCache.class)
                 .setParameter("key", key)
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

    public void save(RequestCache requestCache) {
        em.persist(requestCache);
    }
}