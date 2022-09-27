package com.swp.hr_backend.exception.custom;

import com.swp.hr_backend.model.CustomError;

public class CustomForbiddenExcpetion extends BaseCustomException {

    public CustomForbiddenExcpetion(CustomError customError) {
        super(customError);
    }
    
}
