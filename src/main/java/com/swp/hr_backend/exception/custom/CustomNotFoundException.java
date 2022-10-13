package com.swp.hr_backend.exception.custom;

import com.swp.hr_backend.model.CustomError;

public class CustomNotFoundException extends BaseCustomException {

    public CustomNotFoundException(CustomError customError) {
        super(customError);
    }
    
}
