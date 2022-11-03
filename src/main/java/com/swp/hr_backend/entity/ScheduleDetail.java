package com.swp.hr_backend.entity;

import com.swp.hr_backend.entity.compositeKey.ScheduleDetailID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "scheduleDetail")
public class ScheduleDetail implements Serializable {
    @EmbeddedId
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    ScheduleDetailID scheduleDetailID;

    @Column(name = "status",nullable = false)
    private boolean status;
    @Column(name = "urlMeeting",nullable = true, length = 512)
    private String urlMeeting;
    @Column(name = "start_time" , nullable = false)
    private Time startTime;
    @Column(name = "end_time" , nullable = false)
    private Time endTime;
    @Column(name = "roundNum",nullable = false)
    private String roundNum;

    @ManyToOne
    @MapsId("scheduleID")
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    @MapsId("interviewerID")
    @JoinColumn(name = "account_id")
    private Employee interviewer;

    @ManyToOne
    @MapsId("cvID")
    @JoinColumn(name = "cv_id")
    private UserCV userCV;

    @OneToOne(mappedBy = "scheduleDetail", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "no", nullable = true)
    private Room room;

    public ScheduleDetail(Schedule schedule, UserCV userCV, boolean status, Time startTime, Time endTime, String urlMeeting, String roundNum, Room room){
        this.schedule = schedule;
        this.userCV = userCV;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.urlMeeting = urlMeeting;
        this.roundNum = roundNum;
        this.room = room;
    }
}
