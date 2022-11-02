package com.swp.hr_backend.controller;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.request.OtpRequest;
import com.swp.hr_backend.model.request.SignupRequest;
import com.swp.hr_backend.model.response.AccountResponse;
import com.swp.hr_backend.model.response.OtpResponse;
import com.swp.hr_backend.service.NoteService;
import com.swp.hr_backend.service.PhoneService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/phone")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class PhoneController {
	
	private final PhoneService phoneService;
	
	@PostMapping("/otp")
	public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody OtpRequest otpReq) throws BaseCustomException {
		return new ResponseEntity<OtpResponse>(phoneService.sendOtpMessage(otpReq), HttpStatus.OK);
	}
}
