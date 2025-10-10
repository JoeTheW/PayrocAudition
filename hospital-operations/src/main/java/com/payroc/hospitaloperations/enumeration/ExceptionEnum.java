package com.payroc.hospitaloperations.enumeration;

public enum ExceptionEnum {

    EXCEPTION_4000(4000, "Invalid request", 400),
    EXCEPTION_4040(4040, "Patient not found", 404),
    EXCEPTION_4001(4001, "Patient already discharged", 400),
    EXCEPTION_4002(4002, "Patient already admitted", 400),
    EXCEPTION_5001(5001, "Unable to retrieve cached request response", 500),
	EXCEPTION_5002(5002, "Unable to cache request response", 500);

    private final int code;
    private final String message;
    private final int httpStatus;

    ExceptionEnum(final int code, final String message, final int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}