package com.payroc.hospitaloperations.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.payroc.hospitaloperations.exception.HospitalOperationException;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class HospitalExceptionHandler {

    @ExceptionHandler(HospitalOperationException.class)
    public ResponseEntity<Object> handleHospitalOperationException(HospitalOperationException ex) {
        return ResponseEntity
            .status(ex.getHttpStatus())
            .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    public static class ErrorResponse {
        public final int code;
        public final String message;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}