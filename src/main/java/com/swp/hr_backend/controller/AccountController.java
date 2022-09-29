package com.swp.hr_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.service.AccountService;

import lombok.RequiredArgsConstructor;
@RequestMapping("/api/account")
@RestController
@RequiredArgsConstructor
public class AccountController {
    
	private final AccountService accService;
	
	@GetMapping("/getListAccount")
	public List<Account> getListAccount() {
		return accService.getListAccount();
	}
}
