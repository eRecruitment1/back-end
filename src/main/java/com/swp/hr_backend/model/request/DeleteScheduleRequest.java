package com.swp.hr_backend.model.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeleteScheduleRequest {
    int cvID;
    int scheduleID;
}
