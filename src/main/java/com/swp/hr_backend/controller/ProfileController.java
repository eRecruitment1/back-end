package com.swp.hr_backend.controller;

import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.model.request.ProfileRequest;
import com.swp.hr_backend.model.response.ProfileResponse;
import com.swp.hr_backend.service.AccountService;
import com.swp.hr_backend.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/profile")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class ProfileController {

    private final AccountService accountService;
    private final JwtTokenUtil jwtTokenUtil;

    @GetMapping("/get")
    public ProfileResponse getProfile() {
        return accountService.getProfile(jwtTokenUtil.loggedAccount().getUsername());
    }

    @PutMapping("/edit")
    public ProfileResponse editProfile(@RequestBody ProfileRequest profileRequest) throws CustomDuplicateFieldException {
        return accountService.updateProfile(profileRequest, jwtTokenUtil.loggedAccount().getUsername());
    }
}
