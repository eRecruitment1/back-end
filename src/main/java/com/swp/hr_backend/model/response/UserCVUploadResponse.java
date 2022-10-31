package com.swp.hr_backend.model.response;

import java.sql.Timestamp;

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
public class UserCVUploadResponse {
    private int userCVID;
    private Timestamp applyTime;
    private String linkCV;
    private int postID;
    private String accountID;  
    private String username;
    private String email;
    private String lastName;
    private String firstName;
    private String postTitle;
}
