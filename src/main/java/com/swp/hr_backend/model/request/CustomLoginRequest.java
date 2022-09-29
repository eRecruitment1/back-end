package com.swp.hr_backend.model.request;

import javax.validation.constraints.NotEmpty;

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
public class CustomLoginRequest {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}
