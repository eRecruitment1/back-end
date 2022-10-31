package com.swp.hr_backend.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.swp.hr_backend.entity.Schedule;
import com.swp.hr_backend.entity.ScheduleDetail;
import com.swp.hr_backend.entity.UserCV;
import com.swp.hr_backend.entity.compositeKey.ScheduleDetailID;

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
    
    @Query(value = "SELECT * FROM schedule_detail WHERE schedule_id = ?1 and account_id LIKE ?2 and cv_id = ?3" , nativeQuery = true)
    public ScheduleDetail findByScheduleIDAndAccountIDAndCvId(int scheduleID, String accountId, int cvID);
    public List<ScheduleDetail>  findByUserCV(UserCV userCV);
}
