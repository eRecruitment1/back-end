package com.swp.hr_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoogleAccDTO {
	
	private String name;
	private String picture;
	private String iss;
	private String aud;
	private String auth_time;
	private String user_id;
	private String sub;
	private String iat;
	private String exp;
	private String email;
	private boolean email_verified;

}
