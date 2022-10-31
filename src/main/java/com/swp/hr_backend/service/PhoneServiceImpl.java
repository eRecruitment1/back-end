package com.swp.hr_backend.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.swp.hr_backend.config.TwilioInitiazer;
import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.request.OtpRequest;
import com.swp.hr_backend.model.response.OtpResponse;
import com.swp.hr_backend.repository.AccountRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhoneServiceImpl implements PhoneService{
	
	private final TwilioInitiazer twilio;
	private final AccountRepository accRepo;
	@Override
	public OtpResponse sendOtpMessage(OtpRequest otpReq) throws BaseCustomException {
		if(otpReq.getEmail() == null && otpReq.getPhoneNumber() == null) 
			throw new CustomBadRequestException(CustomError.builder().code("400").message("You need to request with Email or Phone Number!").build());
		Account acc = null;
		if(otpReq.getPhoneNumber() != null) {
			String phone = otpReq.getPhoneNumber().replace("+84", "0");
			acc = accRepo.findByPhone(phone);
		} else {
			acc = accRepo.findByEmail(otpReq.getEmail());
		}
		if(acc == null) throw new CustomNotFoundException(
				CustomError.builder().code("404").message("Not Found Account!").build());
		String otpCode = new Random().nextInt(9999) + "";
		Message.creator(
			    new PhoneNumber(otpReq.getPhoneNumber()),
			    new PhoneNumber(twilio.getTwilioproperties().getPhone()),
			    "This is your Otp code: " + otpCode)
			  .create();
		return OtpResponse.builder().message("Otp Send Success.").otpCode(otpCode).accountId(acc.getAccountID()).build();
	}
	
}
