package com.swp.hr_backend.service;

import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.model.request.OtpRequest;
import com.swp.hr_backend.model.response.OtpResponse;

public interface PhoneService {
	public OtpResponse sendOtpMessage(OtpRequest otpReq) throws BaseCustomException;
}
