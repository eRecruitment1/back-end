package com.swp.hr_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swp.hr_backend.entity.Candidate;
import com.swp.hr_backend.entity.Employee;

public interface CandidateRepository extends JpaRepository<Candidate,String> {
	
}
