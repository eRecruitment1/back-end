package com.swp.hr_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp.hr_backend.entity.Role;

public interface RoleRepository extends JpaRepository<Role,Integer> {
}
