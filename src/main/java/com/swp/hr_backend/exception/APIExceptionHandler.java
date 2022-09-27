package com.swp.hr_backend.exception;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.exception.custom.CustomForbiddenExcpetion;
import com.swp.hr_backend.exception.custom.CustomInternalServerException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.ErrorMessage;

@RestControllerAdvice
public class APIExceptionHandler {
    @ExceptionHandler(CustomDuplicateFieldException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public Map<String, CustomError> duplicateFieldException(CustomDuplicateFieldException ex) {
         return ex.getErrors();
    }
    @ExceptionHandler(CustomBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, CustomError> notFoundIdException(CustomBadRequestException ex) {
        return ex.getErrors();
    }
    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Map<String, CustomError> notFoundIdException(CustomNotFoundException ex) {
        return ex.getErrors();
    }
    @ExceptionHandler(CustomForbiddenExcpetion.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public Map<String, CustomError> forBiddenException(CustomForbiddenExcpetion ex) {
        return ex.getErrors();
    }
    @ExceptionHandler(CustomUnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public Map<String, CustomError> unAuthorizedException(CustomUnauthorizedException ex) {
        return ex.getErrors();
    }
    @ExceptionHandler(CustomInternalServerException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, CustomError> internalServer(CustomInternalServerException ex) {
        return ex.getErrors();
    }
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage notExistException(Exception ex) {
        return new ErrorMessage(404, "Object is not exist: " + ex.getMessage());
    }
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage duplicateException(Exception ex) {
        return new ErrorMessage(409, "Duplicate key in db: " + ex.getMessage());
    }
}
