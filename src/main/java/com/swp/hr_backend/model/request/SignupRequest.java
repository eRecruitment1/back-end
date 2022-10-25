package com.swp.hr_backend.model.request;

import javax.validation.constraints.NotEmpty;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String email;
    private String phone;
    private String firstname;
    private String lastname;
    private String urlImg;
    @NotEmpty
    private boolean gender;
    @Nullable
    private Integer roleID;

}
