package com.swp.hr_backend.exception.custom;

import com.swp.hr_backend.model.CustomError;

public class CustomUnauthorizedException extends BaseCustomException {

    public CustomUnauthorizedException(CustomError customError) {
        super(customError);
    }
    
}
