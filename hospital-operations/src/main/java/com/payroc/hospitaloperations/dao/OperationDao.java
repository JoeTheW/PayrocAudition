package com.payroc.hospitaloperations.dao;


import org.springframework.stereotype.Repository;

import com.payroc.hospitaloperations.entity.Operation;

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

    public void save(Operation operation) {
        em.persist(operation);
    }

    public void update(Operation operation) {
        em.merge( operation );
    }
}