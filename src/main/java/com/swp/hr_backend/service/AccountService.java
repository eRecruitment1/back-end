package com.swp.hr_backend.service;
import java.util.List;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.entity.Candidate;
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.model.request.ProfileRequest;
import com.swp.hr_backend.model.response.ProfileResponse;

public interface AccountService {
    public Account findAccountByUsername(String username);
    
    public ProfileResponse getProfile(String loggedAccount);

    public ProfileResponse updateProfile(ProfileRequest profileRequest, String loggedAccount)
            throws CustomDuplicateFieldException;
    
    public List<Account> getListAccount();
    
    public Candidate createNewCandidate(Candidate acc);
}
