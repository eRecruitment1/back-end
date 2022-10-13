package com.swp.hr_backend.model.request;

import lombok.*;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateScheduleRequest {

    int cvID;
    Date date;
    int newCVID;
    List<String> newInterviewerIDs;
    String newRoundNum;

}
