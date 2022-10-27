package com.swp.hr_backend.service;
import java.util.List;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.entity.Candidate;
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.request.ProfileRequest;
import com.swp.hr_backend.model.request.SignupRequest;
import com.swp.hr_backend.model.response.AccountResponse;
import com.swp.hr_backend.model.response.ProfileResponse;

import javax.mail.MessagingException;

public interface AccountService {
    public Account findAccountByUsername(String username);
    
    public ProfileResponse getProfile(String loggedAccount);

    public ProfileResponse updateProfile(ProfileRequest profileRequest, String loggedAccount)
            throws CustomDuplicateFieldException;
    
    public List<Account> getListAccount();
    
    public Candidate createNewCandidate(Candidate acc);

    public AccountResponse getAccountByID(String id);

    public AccountResponse signUp(SignupRequest signupRequest) throws CustomDuplicateFieldException, MessagingException;

    public AccountResponse createNewEmployee(SignupRequest signupRequest) throws CustomDuplicateFieldException, MessagingException, CustomUnauthorizedException;

    public String verify(String tokenVerify) throws CustomNotFoundException;

    public String sendMailVerify() throws MessagingException, CustomNotFoundException;
    public List<AccountResponse> getEmployee();
}
