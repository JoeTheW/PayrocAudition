package com.payroc.hospitaloperations.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payroc.hospitaloperations.dao.PatientDao;
import com.payroc.hospitaloperations.entity.Patient;

@Service
public class PatientService {

    private final PatientDao patientDao;

    public PatientService(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientDao.findAll();
    }
    
    @Transactional(readOnly = true)
    public Patient getPatientById( Integer id ) {
        return patientDao.getById( id );
    }
    
    @Transactional(readOnly = true)
    public List<Patient> findPatientsByName( final String name ) {
    	return patientDao.findPatientsByName( name );
    }

    @Transactional
    public void admitPatient(final String name) {
        patientDao.save(new Patient(name));
    }
}