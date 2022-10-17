package com.swp.hr_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hr_backend.entity.Account;
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
	
}
