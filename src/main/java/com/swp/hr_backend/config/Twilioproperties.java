package com.swp.hr_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@ConfigurationProperties("twilio")
@NoArgsConstructor
@Getter
@Setter
public class Twilioproperties {
	
	private String accountSid;
	private String authToken;
	private String phone;
}
