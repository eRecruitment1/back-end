package com.swp.hr_backend.exception.custom;

import com.swp.hr_backend.model.CustomError;

public class CustomInternalServerException extends BaseCustomException {

    public CustomInternalServerException(CustomError customError) {
        super(customError);
    }
    
}
