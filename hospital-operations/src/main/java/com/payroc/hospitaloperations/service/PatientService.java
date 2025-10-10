package com.payroc.hospitaloperations.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payroc.hospitaloperations.dao.PatientDao;
import com.payroc.hospitaloperations.entity.Operation;
import com.payroc.hospitaloperations.entity.Patient;

@Service
public class PatientService {

    private final PatientDao patientDao;
    private final OperationService operationService;

    public PatientService(PatientDao patientDao, OperationService operationService) {
        this.patientDao = patientDao;
        this.operationService = operationService;
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

    @Transactional
    public Operation submitPatientDischarge( final String idempotencyKey, final Patient patient ) {
        Operation operation = operationService.submitPatientDischargeOperation( idempotencyKey, patient );
        return operation;
    }
}