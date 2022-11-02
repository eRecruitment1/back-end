package com.swp.hr_backend.controller;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.response.NoteResponse;
import com.swp.hr_backend.service.AccountService;
import com.swp.hr_backend.service.MailService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/mail")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class MailController {
	private final MailService mailService;
	
	@GetMapping(value = "/forgotPassword")
	public ResponseEntity<Boolean> sendForgetPasswordLink(@RequestParam(name = "email") String email) throws BaseCustomException, MessagingException {
		mailService.sendMailForgetPassword(email);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
}
