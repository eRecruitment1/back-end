package com.swp.hr_backend.entity.compositeKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ScheduleDetailID implements Serializable {
    @Column(name = "schedule_id")
    private int scheduleID;
    @Column(name = "account_id")
    private String interviewerID;
    @Column(name = "cv_id")
    private int cvID;
}
