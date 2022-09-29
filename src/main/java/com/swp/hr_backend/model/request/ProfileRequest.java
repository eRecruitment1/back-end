package com.swp.hr_backend.model.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfileRequest {
    private String firstName;
    private String lastName;
    private String urlImg;
    private String phone;
    private boolean gender;
}
