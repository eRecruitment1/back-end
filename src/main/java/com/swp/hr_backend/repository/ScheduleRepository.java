package com.swp.hr_backend.repository;

import com.swp.hr_backend.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    public Schedule findByDate(Date date);
    public Schedule findByScheduleID(int scheduleID);
}
