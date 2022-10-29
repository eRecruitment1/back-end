package com.swp.hr_backend.service;

import java.util.List;

import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.request.EvaluateRequest;
import com.swp.hr_backend.model.request.UserCVUploadRequest;
import com.swp.hr_backend.model.response.UserCVUploadResponse;

public interface CVService {
    public UserCVUploadResponse uploadCV(UserCVUploadRequest cvRequest)
            throws CustomDuplicateFieldException, CustomBadRequestException, CustomUnauthorizedException;

    public List<UserCVUploadResponse> viewCV();
    
    public boolean evaluateUserCV(EvaluateRequest evaluate) throws BaseCustomException;
    public List<UserCVUploadResponse> getCompleted() throws CustomUnauthorizedException;
}
