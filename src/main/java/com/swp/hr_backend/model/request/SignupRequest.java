package com.swp.hr_backend.model.request;

import lombok.*;
import org.springframework.lang.Nullable;
import javax.validation.constraints.NotEmpty;

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
    private int roleID;

}
