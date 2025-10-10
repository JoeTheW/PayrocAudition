package com.payroc.hospitaloperations.util;

import com.payroc.hospitaloperations.enumeration.ExceptionEnum;
import com.payroc.hospitaloperations.exception.HospitalOperationException;

public class ValidationUtils {
    
    // Utility method to validate if a field is not null
    public static void validateMandatoryField(Object field, String fieldName) {
        if (field == null) {
            throw new HospitalOperationException(ExceptionEnum.EXCEPTION_4000, fieldName + " is mandatory");
        }
    }
}