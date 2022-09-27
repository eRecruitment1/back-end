package com.swp.hr_backend.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Integer id;
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer roleID;
    private String firstname;
    private String lastname;
    private String urlImg;
    private String email;
    private String phone;
    private boolean gender;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int status;
}
