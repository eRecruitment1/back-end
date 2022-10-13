package com.swp.hr_backend.exception.custom;

import com.swp.hr_backend.model.CustomError;

public class CustomDuplicateFieldException  extends BaseCustomException{

    public CustomDuplicateFieldException(CustomError customError) {
        super(customError);
    }
    
}
