package com.payroc.hospitaloperations.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.payroc.hospitaloperations.entity.Patient;

@Repository
public class PatientDao {

    @PersistenceContext
    private EntityManager em;

    public List<Patient> findAll() {
        return em.createNamedQuery(Patient.FIND_ALL, Patient.class)
                 .getResultList();
    }

    public Patient getById(Integer id) {
        return em.createNamedQuery(Patient.GET_BY_ID, Patient.class)
                 .setParameter("id", id)
                 .getSingleResult();
    }
    
    public List<Patient> findPatientsByName(String name) {
    	return em.createNamedQuery(Patient.FIND_BY_NAME, Patient.class)
    			.setParameter("name", name)
    			.getResultList();
    }

    public void save(Patient patient) {
        em.persist(patient);
    }
}