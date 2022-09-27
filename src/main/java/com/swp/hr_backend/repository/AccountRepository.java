package com.swp.hr_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swp.hr_backend.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account,String>{
    public Optional<Account> findByUsername(String username);
    public Account findByPhone(String phone);
    public Account findByEmail(String email);
}
