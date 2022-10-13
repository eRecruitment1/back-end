package com.swp.hr_backend.repository;

import com.swp.hr_backend.entity.Candidate;
import com.swp.hr_backend.entity.UserCV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCVRepository extends JpaRepository<UserCV, Integer> {
    public UserCV findByCvID(int cvID);
    public UserCV findByCandidate(Candidate candidate); 
}
