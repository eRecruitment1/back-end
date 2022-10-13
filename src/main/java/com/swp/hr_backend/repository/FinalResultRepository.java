package com.swp.hr_backend.repository;

import com.swp.hr_backend.entity.FinalResult;
import com.swp.hr_backend.entity.UserCV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinalResultRepository extends JpaRepository<FinalResult, Integer> {
    FinalResult findByUserCV(UserCV userCV);
}
