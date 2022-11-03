package com.swp.hr_backend.repository;

import com.swp.hr_backend.entity.Room;
import com.swp.hr_backend.entity.Schedule;
import com.swp.hr_backend.entity.ScheduleDetail;
import com.swp.hr_backend.entity.compositeKey.ScheduleDetailID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Time;
import java.util.List;

@Repository
public interface ScheduleDetailRepository extends CrudRepository<ScheduleDetail, ScheduleDetailID> {
    public List<ScheduleDetail> findByScheduleDetailIDInterviewerID(String interviewerID);
    public List<ScheduleDetail> findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(int scheduleID, int cvID);
    public List<ScheduleDetail> findByScheduleDetailIDCvID(int cvID);
    public List<ScheduleDetail> findAllBySchedule(Schedule schedule);
    public List<ScheduleDetail> findByScheduleDetailIDCvIDAndRoundNum(int cvID, String roundNum);

    @Transactional
    @Modifying
    @Query(value = "Delete from schedule_detail WHERE cv_id=?2 And schedule_id = ?1 And account_id LIKE ?3" , nativeQuery = true)
    public void deleteByScheduleIDAndCVIDAndInterviewerID(int scheduleID, int cvID, String interviewerID);
    
    @Query(value = "SELECT * FROM schedule_detail WHERE schedule_id = ?1 and account_id LIKE ?2 and cv_id = ?3" , nativeQuery = true)
    public ScheduleDetail findByScheduleIDAndAccountIDAndCvId(int scheduleID, String accountId, int cvID);

    @Query(value = "SELECT * FROM schedule_detail WHERE no = ?1 and status = ?2 and start_time = ?3 and end_time = ?4" , nativeQuery = true)
    public ScheduleDetail findByNoAndStatusAndStartTimeAndEndTime(int no, boolean status, Time startTime, Time endTime);

    public ScheduleDetail findByUrlMeetingAndStatusAndStartTimeAndEndTime(String urlMeeting, boolean status, Time startTime, Time endTime);
}
