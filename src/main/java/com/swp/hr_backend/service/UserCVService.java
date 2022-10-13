package com.swp.hr_backend.service;

import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.model.request.EvaluateRequest;

public interface UserCVService {
	public boolean evaluateUserCV(EvaluateRequest evaluate) throws BaseCustomException;
}
