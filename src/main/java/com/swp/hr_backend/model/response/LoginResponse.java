package com.swp.hr_backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String id;
    private String username;
    private String roleName;
    private String firstName;
    private String lastName;
    private String urlImg;
    private String email;
    private String phone;
    private boolean gender;
    private boolean status;
    private String token;
    private String refreshToken;
}
