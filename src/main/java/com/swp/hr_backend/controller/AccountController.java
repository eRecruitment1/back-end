package com.swp.hr_backend.controller;

import java.util.List;

import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.request.SignupRequest;
import com.swp.hr_backend.model.response.ScheduleDetailResponse;
import com.swp.hr_backend.utils.JwtTokenUtil;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.model.response.AccountResponse;
import com.swp.hr_backend.service.AccountService;

import lombok.RequiredArgsConstructor;

import javax.mail.MessagingException;

@RequestMapping("/api/account")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AccountController {
    
	private final AccountService accService;
	
	@GetMapping("/getListAccount")
	public List<Account> getListAccount() {
		return accService.getListAccount();
	}
	@GetMapping(value="/{id}")
	public ResponseEntity<AccountResponse> getAccountByID(@PathVariable("id") String id) {
		if(accService.getAccountByID(id) == null){
			return null;
		}
	    return ResponseEntity.ok(accService.getAccountByID(id));	
	}

	@PostMapping("/signup")
	public ResponseEntity<AccountResponse> signUp(@RequestBody SignupRequest signupRequest)
		throws CustomDuplicateFieldException, CustomBadRequestException, MessagingException {
		AccountResponse accountResponse = accService.signUp(signupRequest);
		if (accountResponse == null) throw new CustomBadRequestException(CustomError.builder().code("403").message("Account is null").build());
		return ResponseEntity.ok(accountResponse);
	}

	@PostMapping("/createAccount")
	public ResponseEntity<AccountResponse> createAccount(@RequestBody SignupRequest signupRequest)
		throws CustomDuplicateFieldException, CustomBadRequestException, MessagingException, CustomUnauthorizedException {
		AccountResponse accountResponse = accService.createNewEmployee(signupRequest);
		if (accountResponse == null) throw new CustomBadRequestException(CustomError.builder().code("403").message("Account is null").build());
		return ResponseEntity.ok(accountResponse);
	}

	@GetMapping("/verify")
	public String verifyAccount(@RequestParam("token") String tokenVerify) throws CustomNotFoundException {
		String announce = accService.verify(tokenVerify);
		return announce;
	}

	@GetMapping("/sendMailVerify")
	public String sendMailVerify() throws MessagingException, CustomNotFoundException{
		return accService.sendMailVerify();
	}
}
