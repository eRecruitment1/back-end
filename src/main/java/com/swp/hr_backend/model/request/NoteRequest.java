package com.swp.hr_backend.model.request;

import org.springframework.lang.Nullable;

import com.swp.hr_backend.model.response.NoteResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
public class NoteRequest {
	@Nullable
	private Integer id;
	private String message;
	private int point;
	private int scheduleId;
	private int cvId;	
}
