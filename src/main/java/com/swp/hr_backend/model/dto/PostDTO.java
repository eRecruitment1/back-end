package com.swp.hr_backend.model.dto;

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
    @Nullable
	@JsonProperty("postId")
	private Integer postId;
	@Nullable
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
	@Nullable
	@JsonProperty("accountId")
	private String accountId;
}
