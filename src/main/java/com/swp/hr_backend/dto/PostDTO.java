package com.swp.hr_backend.dto;

import java.sql.Timestamp;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
public class PostDTO {
	@JsonProperty("postId")
	@Nullable
	private int postId;
	@JsonProperty("startTime")
	private Timestamp startTime;
	@JsonProperty("title")
	private String title;
	@JsonProperty("description")
	private String description;
	@JsonProperty("thumbnailUrl")
	private String thumbnailUrl;
	@JsonProperty("status")
	private boolean status;
	@JsonProperty("accountId")
	private String accountId;
}
