package com.swp.hr_backend.controller;

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
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.request.EvaluateRequest;
import com.swp.hr_backend.model.request.NoteRequest;
import com.swp.hr_backend.model.response.NoteResponse;
import com.swp.hr_backend.service.PostService;
import com.swp.hr_backend.service.UserCVService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/usercv")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class UserCVController {
	private final UserCVService userCVService;
	
	@PostMapping("/evaluate")
	public ResponseEntity<Boolean> takeNote(@Valid @RequestBody EvaluateRequest evaReq) throws BaseCustomException {
		boolean result = userCVService.evaluateUserCV(evaReq);
		if (!result) {
			throw new CustomBadRequestException(CustomError.builder().code("403").message("evaluate failed...").build());
		} else {
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
	}
}
