package com.swp.hr_backend.model.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateScheduleRequest {

    @NotEmpty
    Date date;
    @NotEmpty
    List<String> interviewerIDs;
    @NotEmpty
    String round;
    @NotEmpty
    String urlMeeting;
    @NotEmpty
    int cvID;
    @NotEmpty
    Time startTime;
    @NotEmpty
    Time endTime;

}
