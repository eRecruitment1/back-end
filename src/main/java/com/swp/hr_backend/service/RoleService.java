package com.swp.hr_backend.service;

import java.util.Optional;

public interface RoleService {
    public  Optional<String> findRolenameByRoleID(int role_id);
}
