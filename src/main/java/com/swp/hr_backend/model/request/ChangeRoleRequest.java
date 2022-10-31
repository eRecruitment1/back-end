package com.swp.hr_backend.model.request;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

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
public class ChangeRoleRequest {
	private String accountId;
	private String role;
}
