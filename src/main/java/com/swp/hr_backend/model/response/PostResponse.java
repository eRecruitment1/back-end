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
public class PostResponse {
    private Timestamp startTime;
    private String title;
    private String description;
    private String thumbnailUrl;
    private boolean status;
}
