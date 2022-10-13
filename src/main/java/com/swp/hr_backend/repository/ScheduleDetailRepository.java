package com.swp.hr_backend.repository;

import com.swp.hr_backend.entity.Schedule;
import com.swp.hr_backend.entity.ScheduleDetail;
import com.swp.hr_backend.entity.compositeKey.ScheduleDetailID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ScheduleDetailRepository extends CrudRepository<ScheduleDetail, ScheduleDetailID> {
    public List<ScheduleDetail> findByScheduleDetailIDInterviewerID(String interviewerID);
    public List<ScheduleDetail> findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(int scheduleID, int cvID);
    public List<ScheduleDetail> findByScheduleDetailIDCvID(int cvID);

    public List<ScheduleDetail> findAllBySchedule(Schedule schedule);

    @Transactional
    @Modifying
    @Query(value = "Delete from schedule_detail WHERE cv_id=?2 And schedule_id = ?1 And account_id LIKE ?3" , nativeQuery = true)
    public void deleteByScheduleIDAndCVIDAndInterviewerID(int scheduleID, int cvID, String interviewerID);
}
