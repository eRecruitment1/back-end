package com.swp.hr_backend.model.response;

import lombok.*;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDetailResponse {
    private int scheduleID;
    private Date date;
    private List<String> interviewerID;
    private int cvID;
    private String roundNum;
    private Time startTime;
    private Time endTime;
    private boolean status;
    private String urlMeeting;
    private List<AccountResponse> accountResponses;
}
