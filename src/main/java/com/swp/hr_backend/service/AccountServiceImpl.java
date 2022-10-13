package com.swp.hr_backend.service;

import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.model.request.ProfileRequest;
import com.swp.hr_backend.model.response.ProfileResponse;

import java.util.List;

import org.springframework.stereotype.Service;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.entity.Candidate;
import com.swp.hr_backend.repository.AccountRepository;
import com.swp.hr_backend.repository.CandidateRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;
	private final CandidateRepository CandRepository;

	@Override
	public Account findAccountByUsername(String username) {
		Account account  = accountRepository.findByUsername(username) ;
		if(account != null){
			return account;
		}
		return null;
	}

	public ProfileResponse getProfile(String loggedAccount) {
		Account account = findAccountByUsername(loggedAccount);
		return ObjectMapper.accountToProfileResponse(account);
	}

	public ProfileResponse updateProfile(ProfileRequest profileRequest, String loggedAccount)
			throws CustomDuplicateFieldException {
		Account account = findAccountByUsername(loggedAccount);
		String firstName = StringUtils.trimWhitespace(profileRequest.getFirstName());
		if (firstName != null && !firstName.equals(account.getFirstname()) && !firstName.isEmpty()) {
			account.setFirstname(firstName);
		}

		String lastName = StringUtils.trimWhitespace(profileRequest.getLastName());
		if (lastName != null && !lastName.equals(account.getLastname()) && !lastName.isEmpty()) {
			account.setLastname(lastName);
		}

		String urlImg = StringUtils.trimWhitespace(profileRequest.getUrlImg());
		if (urlImg != null && !urlImg.equals(account.getUrlImg()) && !urlImg.isEmpty()) {
			account.setUrlImg(urlImg);
		}

		String phone = StringUtils.trimWhitespace(profileRequest.getPhone());
		if (phone != null && !phone.equals(account.getPhone())) {
			account.setPhone(phone);
		} else {
			phone = null;
		}

		boolean gender = profileRequest.isGender();
		account.setGender(gender);
		checkDuplicate(null, phone);
		account = accountRepository.save(account);
		return ObjectMapper.accountToProfileResponse(account);
	}

	public void checkDuplicate(String email, String phone) throws CustomDuplicateFieldException {
		Account duplicate;

		if (email != null) {
			duplicate = accountRepository.findByEmail(email);
			if (duplicate != null) {
				throw new CustomDuplicateFieldException(
						CustomError.builder().code("duplicate").field("email").message("Duplicate field").build());
			}
		}

		if (phone != null) {
			duplicate = accountRepository.findByPhone(phone);
			if (duplicate != null) {
				throw new CustomDuplicateFieldException(
						CustomError.builder().code("duplicate").field("phone").message("Duplicate field").build());
			}
		}
	}

	@Override
	public List<Account> getListAccount() {
		return accountRepository.findAll();
	}

	@Override
	public Candidate createNewCandidate(Candidate cand) {
		if (cand != null) {
			CandRepository.save(cand);
			return cand;
		}
		return null;
	}

}
