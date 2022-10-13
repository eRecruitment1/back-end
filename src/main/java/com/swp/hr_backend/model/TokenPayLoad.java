package com.swp.hr_backend.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenPayLoad {
    private String roleName;
}
