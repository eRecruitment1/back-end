package com.swp.hr_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.mail.MessagingException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.entity.Candidate;
import com.swp.hr_backend.entity.Employee;
import com.swp.hr_backend.entity.Role;
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.model.request.DataMailRequest;
import com.swp.hr_backend.model.request.ProfileRequest;
import com.swp.hr_backend.model.request.SignupRequest;
import com.swp.hr_backend.model.response.AccountResponse;
import com.swp.hr_backend.model.response.ProfileResponse;
import com.swp.hr_backend.repository.AccountRepository;
import com.swp.hr_backend.repository.CandidateRepository;
import com.swp.hr_backend.repository.EmployeeRepository;
import com.swp.hr_backend.repository.RoleRepository;
import com.swp.hr_backend.utils.AccountRole;
import com.swp.hr_backend.utils.MailBody;
import com.swp.hr_backend.utils.MailSubjectConstant;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;
	private final CandidateRepository CandRepository;
	private final RoleRepository roleRepository;
	private final EmployeeRepository employeeRepository;
	private final MailService mailService;

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
		checkDuplicate(null, phone, null);
		account = accountRepository.save(account);
		return ObjectMapper.accountToProfileResponse(account);
	}

	public void checkDuplicate(String email, String phone, String username) throws CustomDuplicateFieldException {
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

		if (username != null) {
			duplicate = accountRepository.findByUsername(username);
			if (duplicate != null) {
				throw new CustomDuplicateFieldException(
						CustomError.builder().code("duplicate").field("username").message("Duplicate field").build());
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

	@Override
	public AccountResponse getAccountByID(String id) {
	    Optional<Account> account = accountRepository.findById(id);
		if(account.isEmpty()){
			return null;
		}
		return ObjectMapper.accountToAccountResponse(account.get());
	}

	@Override
	public AccountResponse signUp(SignupRequest signupRequest) throws CustomDuplicateFieldException, MessagingException {
		checkDuplicate(signupRequest.getEmail(), signupRequest.getPhone(), signupRequest.getUsername());
		Candidate candidate = new Candidate();
		candidate.setAccountID(UUID.randomUUID().toString());
		candidate.setUsername(signupRequest.getUsername());
		candidate.setPassword(signupRequest.getPassword());
		candidate.setEmail(signupRequest.getEmail());
		candidate.setPhone(signupRequest.getPhone());
		candidate.setFirstname(signupRequest.getFirstname());
		candidate.setLastname(signupRequest.getLastname());
		candidate.setUrlImg(signupRequest.getUrlImg());
		candidate.setGender(signupRequest.isGender());
		candidate.setStatus(true);
		DataMailRequest dataMailRequest = new DataMailRequest();
		dataMailRequest.setTo(signupRequest.getEmail());
		dataMailRequest.setSubject(MailSubjectConstant.REGISTER);
		String token = RandomString.make(64);
		candidate.setTokenVerify(token);
		candidate.setEnabled(false);
		candidate = CandRepository.save(candidate);
		String link = "https://we-hr-system.herokuapp.com/api/account/verify?token=" + token;
		mailService.sendHtmlMail(dataMailRequest, MailBody.VerifyRegistration(signupRequest.getFirstname(), link));
		return ObjectMapper.accountToAccountResponse(candidate);
	}

	@Override
	public AccountResponse createNewEmployee(SignupRequest signupRequest) throws CustomDuplicateFieldException, MessagingException, CustomUnauthorizedException {
		checkDuplicate(signupRequest.getEmail(), signupRequest.getPhone(), signupRequest.getUsername());
		Account acc = loggedAccount();
		if (employeeRepository.findByAccountID(acc.getAccountID())!=null && employeeRepository.findByAccountID(acc.getAccountID()).getRole().getRoleName().equals(AccountRole.ADMIN.toString())) {
			Employee employee = new Employee();
			employee.setAccountID(UUID.randomUUID().toString());
			employee.setUsername(signupRequest.getUsername());
			employee.setPassword(signupRequest.getPassword());
			employee.setEmail(signupRequest.getEmail());
			employee.setPhone(signupRequest.getPhone());
			employee.setFirstname(signupRequest.getFirstname());
			employee.setLastname(signupRequest.getLastname());
			employee.setUrlImg(signupRequest.getUrlImg());
			employee.setGender(signupRequest.isGender());
			employee.setRole(roleRepository.findByRoleID(signupRequest.getRoleID()));
			employee.setStatus(true);
			DataMailRequest dataMailRequest = new DataMailRequest();
			dataMailRequest.setTo(signupRequest.getEmail());
			dataMailRequest.setSubject(MailSubjectConstant.REGISTER);
			String token = RandomString.make(64);
			employee.setTokenVerify(token);
			employee.setEnabled(false);
			employee = employeeRepository.save(employee);
			String link = "https://we-hr-system.herokuapp.com/api/account/verify?token=" + token;
			mailService.sendHtmlMail(dataMailRequest, MailBody.VerifyEmployee(signupRequest.getFirstname(), link));
			return ObjectMapper.accountToAccountResponse(employee);
		} else throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
				.message("Access denied, you need to be ADMIN to do this!").build());
	}

	@Override
	public String verify(String tokenVerify) throws CustomNotFoundException {
		Account acc = accountRepository.findByTokenVerify(tokenVerify);
		if (acc == null)
			throw new CustomNotFoundException(CustomError.builder().code("404").message("not found token").build());
		if (acc.isEnabled()) return "This account has been already verified before.";
		acc.setEnabled(true);
		accountRepository.save(acc);
		return "Verified";
	}

	@Override
	public String sendMailVerify() throws MessagingException, CustomNotFoundException{
		Account account = loggedAccount();
		if (account == null)
			throw new CustomNotFoundException(CustomError.builder().code("404").message("not found token").build());
		if (account.isEnabled()) return "This account has been already verified before.";
		DataMailRequest dataMailRequest = new DataMailRequest();
		dataMailRequest.setTo(account.getEmail());
		dataMailRequest.setSubject(MailSubjectConstant.REGISTER);
		String link = "https://we-hr-system.herokuapp.com/api/account/verify?token=" + account.getTokenVerify();
		mailService.sendHtmlMail(dataMailRequest, MailBody.VerifyRegistration(account.getFirstname(), link));
		return "Sent";
	}

	public Account loggedAccount() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			Account account = accountRepository.findByUsername(username);
			return account;
		}
		return null;
	}

	@Override
	public List<AccountResponse> getEmployee(int roleID) {
		List<AccountResponse> accountResponses = new ArrayList<>();
		Role role = roleRepository.findByRoleID(roleID);
		List<Employee> employees = employeeRepository.findByRole(role);
		for (Employee employee : employees) {
			Account account = accountRepository.findById(employee.getAccountID()).get();
			AccountResponse accountResponse = ObjectMapper.accountToAccountResponse(account);
			accountResponses.add(accountResponse);
		}
		return accountResponses;
	}


}
