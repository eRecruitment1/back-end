package com.swp.hr_backend.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.model.request.LoginRequest;
import com.swp.hr_backend.model.request.RefreshTokenRequest;
import com.swp.hr_backend.model.response.LoginResponse;
import com.swp.hr_backend.model.response.RefreshTokenResponse;
import com.swp.hr_backend.service.AccountService;
import com.swp.hr_backend.service.EmployeeService;
import com.swp.hr_backend.service.RoleService;
import com.swp.hr_backend.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class AuthenController {
    // private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final AccountService accountService;
    private final EmployeeService employeeService;
    private final RoleService roleService;

    // private void authenticate(String username, String password) throws Exception
    // {
    // try {
    // authenticationManager.authenticate(new
    // UsernamePasswordAuthenticationToken(username, password));
    // } catch (DisabledException e) {
    // throw new Exception("USER_DISABLED", e);
    // } catch (BadCredentialsException e) {
    // throw new
    // CustomUnauthorizedException(CustomError.builder().code("unauthorized").message("Unauthorized").build());
    // }
    // }
    @PostMapping(value = "/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {

        // authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        // truy xuất vào db để check login

        final LoginResponse loginResponse = ObjectMapper
                .accountToLoginResponse(accountService.findAccountByUsername(loginRequest.getUsername()));
        Account account = accountService.findAccountByUsername(loginRequest.getUsername());
        boolean isAuthen = false;
        String roleName = null;
        if(account != null){
            if(loginRequest.getPassword().equals(account.getPassword())){
                isAuthen = true;
            }
            Optional<String> roleNameOptional = Optional.empty();
            if(isAuthen){
                Integer roleID = employeeService.findRoleIDByAccountID(account.getAccountID());
                if(roleID != null){
                    roleNameOptional = roleService.findRolenameByRoleID(roleID);
                    roleName = roleNameOptional.get();
                } else{
                    roleName = "Candidate";
                }
                loginResponse.setRoleName(roleName);
            }
        }

        if (!loginResponse.isStatus() || ! isAuthen) {
            throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
                    .message("Access denied, you are deactivate").build());
        }

        final String accessToken = jwtTokenUtil.generateToken(loginResponse.getUsername(),
                JwtTokenUtil.ACCESS_TOKEN_EXPIRED, roleName);
        final String refreshToken = jwtTokenUtil.generateToken(loginResponse.getUsername(),
                JwtTokenUtil.REFRESH_TOKEN_EXPIRED, roleName);
        loginResponse.setToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        return ResponseEntity.ok(loginResponse);
    }
    @PostMapping("refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest tokenRequest){
        if(!jwtTokenUtil.validateToken(tokenRequest.getRefreshToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
        String username = jwtTokenUtil.getUsernameFromToken(tokenRequest.getRefreshToken());
        Account account = accountService. findAccountByUsername(username);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token claim is invalid");
        }
        String roleName = jwtTokenUtil.getTokenPayLoad(tokenRequest.getRefreshToken()).getRoleName();
        String newToken = jwtTokenUtil.generateToken(username,JwtTokenUtil.ACCESS_TOKEN_EXPIRED, roleName);
        String newRefreshToken = jwtTokenUtil.generateToken(username,JwtTokenUtil.REFRESH_TOKEN_EXPIRED, roleName);
        RefreshTokenResponse response = new RefreshTokenResponse(newToken, newRefreshToken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

}
