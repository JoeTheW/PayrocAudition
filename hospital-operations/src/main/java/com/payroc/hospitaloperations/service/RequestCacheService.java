package com.payroc.hospitaloperations.service;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.hospitaloperations.dao.RequestCacheDao;
import com.payroc.hospitaloperations.dto.RequestCacheResponseDTO;
import com.payroc.hospitaloperations.entity.RequestCache;
import com.payroc.hospitaloperations.enumeration.ExceptionEnum;
import com.payroc.hospitaloperations.exception.HospitalOperationException;

@Service
public class RequestCacheService {
	private static final Logger logger = LoggerFactory.getLogger(RequestCacheService.class);

    private final RequestCacheDao requestCacheDao;

    public RequestCacheService(RequestCacheDao requestCacheDao) {
        this.requestCacheDao = requestCacheDao;
    }

    @Transactional(readOnly = true)
    public RequestCache getRequestCacheByKey( String compositeKey ) {
        return requestCacheDao.getByKey( compositeKey );
    }
    
    public RequestCacheResponseDTO getRequestCacheResponseAsDTO( RequestCache requestCache )
    throws HospitalOperationException
    {
    	try {
        	////Return stored response
    		String requestResponse = requestCache.getResponse();
    		// Convert response json string to RequestCacheResponseDTO
    		ObjectMapper objectMapper = new ObjectMapper();
        	RequestCacheResponseDTO responseDTO = objectMapper.readValue(requestResponse, RequestCacheResponseDTO.class);
    		return responseDTO;
		} catch (Exception e) {
			// log error
			logger.error("Error parsing cached response: {}", e.getMessage(), e);
			throw new HospitalOperationException( ExceptionEnum.EXCEPTION_5001 );
		}
    }

    @Transactional
	public RequestCache cacheRequest(String compositeKey, RequestCacheResponseDTO responseDTO) 
	throws HospitalOperationException 
	{
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String responseJson = objectMapper.writeValueAsString( responseDTO );
			RequestCache requestCache = new RequestCache( compositeKey, responseJson );
			requestCacheDao.save( requestCache ); 
			return requestCache;
		} catch (Exception e) {
			logger.error("Error parsing cached response: {}", e.getMessage(), e);
			throw new HospitalOperationException( ExceptionEnum.EXCEPTION_5001 );
		}
	}
}