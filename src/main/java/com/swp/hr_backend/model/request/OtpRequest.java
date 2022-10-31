package com.swp.hr_backend.model.request;

import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {
	@Nullable
	private String phoneNumber;
	@Nullable
	private String email;
}
