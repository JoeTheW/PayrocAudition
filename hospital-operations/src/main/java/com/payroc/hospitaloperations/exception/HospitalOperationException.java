package com.payroc.hospitaloperations.exception;

import com.payroc.hospitaloperations.enumeration.ExceptionEnum;

public class HospitalOperationException extends RuntimeException {
	
	private static final long serialVersionUID = 5952558977845046164L;
	
	private final ExceptionEnum exceptionEnum;

    public HospitalOperationException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.exceptionEnum = exceptionEnum;
    }

    public HospitalOperationException(ExceptionEnum exceptionEnum, String customMessage) {
        super(exceptionEnum.getMessage() + ": " + customMessage);
        this.exceptionEnum = exceptionEnum;
    }

    public int getCode() {
        return exceptionEnum.getCode();
    }

    public int getHttpStatus() {
        return exceptionEnum.getHttpStatus();
    }

    public ExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }
}