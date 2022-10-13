package com.swp.hr_backend.model.response;

import lombok.*;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String urlImg;
    private String email;
    private String phone;
    private boolean gender;
}
