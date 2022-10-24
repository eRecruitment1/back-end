package com.swp.hr_backend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataMailRequest {
    private String to;
    private String subject;
    private String content;
    private Map<String, Object> props;
}
