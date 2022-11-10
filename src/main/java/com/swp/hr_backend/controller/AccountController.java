package com.swp.hr_backend.controller;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.request.ChangeRoleRequest;
import com.swp.hr_backend.model.request.ForgotPasswordRequest;
import com.swp.hr_backend.model.request.SignupRequest;
import com.swp.hr_backend.model.response.AccountResponse;
import com.swp.hr_backend.service.AccountService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/account")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AccountController {
    
	private final AccountService accService;
	
	@GetMapping("/getListAccount")
	public List<AccountResponse> getListAccount() {
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
	@GetMapping("/getAccountByRole/{roleName}")
	public ResponseEntity<List<AccountResponse>> getEmployee(@PathVariable("roleName") String roleName) throws CustomNotFoundException, CustomBadRequestException{
		int roleID;
		if(roleName.equalsIgnoreCase("EMPLOYEE")){
			roleID = 1;
		} else if(roleName.equalsIgnoreCase("HREMPLOYEE")){
			roleID = 2;
		} else if(roleName.equalsIgnoreCase("HRMANAGER")){
			roleID = 3;
		} else if(roleName.equalsIgnoreCase("ADMIN")){
			roleID = 4;
		}else {
			throw new CustomBadRequestException(CustomError.builder().code("400").message("Not Have Role You Want").build());
		}
		List<AccountResponse> accountResponses = null;
		accountResponses = accService.getEmployee(roleID);
		if(accountResponses.isEmpty()){
			throw new CustomNotFoundException(CustomError.builder().code("404").message("Not Found Anything").build());
		}
		return ResponseEntity.ok(accountResponses);
	}
	
	@PostMapping("/forgotPassword")
	public ResponseEntity<Boolean> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordReq) throws BaseCustomException {
		boolean result = accService.forgotPassword(forgotPasswordReq);
		if(!result) {
			throw new CustomNotFoundException(CustomError.builder().code("403").message("Account is null").build());
		}
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/changeRole")
	public ResponseEntity<Boolean> changeRole(@RequestBody ChangeRoleRequest changeRoleReq) throws BaseCustomException {
		boolean result = accService.changeAccountRole(changeRoleReq);
		if(result) {
		return ResponseEntity.ok(true);
		} else {
			return new ResponseEntity<Boolean>(result, HttpStatus.BAD_REQUEST);
		}
	}
	
}
