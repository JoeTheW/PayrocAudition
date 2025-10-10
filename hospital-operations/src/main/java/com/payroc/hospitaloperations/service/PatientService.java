package com.payroc.hospitaloperations.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payroc.hospitaloperations.dao.PatientDao;
import com.payroc.hospitaloperations.entity.Patient;
import com.payroc.hospitaloperations.enumeration.ExceptionEnum;
import com.payroc.hospitaloperations.enumeration.PatientStatusEnum;
import com.payroc.hospitaloperations.exception.HospitalOperationException;

@Service
public class PatientService {
	private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
	
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
    
    @Transactional
	public void performPatientDischargeOperation( final Integer patientId ) 
    {
    	logger.info("Discharging patient: " + patientId);
    	Patient patient = getPatientById(patientId);
    	if ( patient == null )
    	{
    		throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4040);
    	}
    	
    	if ( patient.getStatus().equals( PatientStatusEnum.DISCHARGED ))
    	{
    		throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4001);
    	}
    	
    	patient.setStatus( PatientStatusEnum.DISCHARGED );
        patientDao.updatePatient(patient);
	}
    
    @Transactional
    public void performUndoPatientDischargeOperation( final Integer patientId ) 
    {
    	logger.info("Undoing patient discharge for patient: " + patientId);
    	Patient patient = getPatientById(patientId);
    	if ( patient == null )
    	{
    		throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4040);
    	}
    	if ( patient.getStatus().equals( PatientStatusEnum.ADMITTED ))
    	{
    		throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4002);
    	}
    	
    	patient.setStatus( PatientStatusEnum.ADMITTED );
    	patientDao.updatePatient(patient);
    }
}