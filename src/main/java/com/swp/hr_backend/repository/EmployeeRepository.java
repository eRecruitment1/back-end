package com.swp.hr_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swp.hr_backend.entity.Employee;
import com.swp.hr_backend.entity.Role;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,String> {
    public Employee findByAccountID(String account_id);
    public List<Employee>  findByRole(Role role);
}
