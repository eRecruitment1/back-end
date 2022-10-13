package com.swp.hr_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swp.hr_backend.entity.Account;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>{
    public Account findByUsername(String username);
    public Account findByPhone(String phone);
    public Account findByEmail(String email);
}
