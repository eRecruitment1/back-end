package com.swp.hr_backend.model.response;

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
public class NoteResponse {
	private int id;
	private String message;
	private int point;
	private int scheduleId;
	private String accountId;
	private int cvId;
	private String firstName;
	private String lastName;
	private String userName;
	private String email;
	private String linkCV;
}
