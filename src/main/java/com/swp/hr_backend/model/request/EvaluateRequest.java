package com.swp.hr_backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EvaluateRequest {
	private int cvId;
	private int scheduleId;
	private String isPass;
}
