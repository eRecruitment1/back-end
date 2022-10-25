package com.swp.hr_backend.controller;

import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.request.CreateScheduleRequest;
import com.swp.hr_backend.model.request.DeleteScheduleRequest;
import com.swp.hr_backend.model.request.UpdateScheduleRequest;
import com.swp.hr_backend.model.response.ScheduleDetailResponse;
import com.swp.hr_backend.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/schedule")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/view")
    public ResponseEntity<List<ScheduleDetailResponse>> viewSchedule() throws CustomUnauthorizedException, CustomNotFoundException {
        List<ScheduleDetailResponse> scheduleResponses = scheduleService.getSchedule();
        return ResponseEntity.ok(scheduleResponses);
    }

    @PostMapping("/create")
    public ResponseEntity<ScheduleDetailResponse> createSchedule(@RequestBody CreateScheduleRequest createScheduleRequest)
            throws CustomBadRequestException, CustomUnauthorizedException{
        ScheduleDetailResponse scheduleResponses = scheduleService.createSchedule(createScheduleRequest);
        if (scheduleResponses == null) throw new CustomBadRequestException(CustomError.builder().code("403").message("Schedule is null").build());
        return ResponseEntity.ok(scheduleResponses);
    }

    @PostMapping("/update")
    public ResponseEntity<ScheduleDetailResponse> updateSchedule(@RequestBody UpdateScheduleRequest updateScheduleRequest)
            throws CustomBadRequestException, CustomUnauthorizedException{
        ScheduleDetailResponse scheduleDetailResponse = scheduleService.updateSchedule(updateScheduleRequest);
        if (scheduleDetailResponse == null) throw new CustomBadRequestException(CustomError.builder().code("403").message("Schedule is null").build());
        return ResponseEntity.ok(scheduleDetailResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ScheduleDetailResponse> deleteSchedule(@RequestBody DeleteScheduleRequest deleteScheduleRequest)
            throws CustomBadRequestException, CustomUnauthorizedException{
        ScheduleDetailResponse scheduleDetailResponse = scheduleService.deleteSchedule(deleteScheduleRequest);
        if (scheduleDetailResponse == null) throw new CustomBadRequestException(CustomError.builder().code("403").message("Schedule is null").build());
        return ResponseEntity.ok(scheduleDetailResponse);
    }
}
