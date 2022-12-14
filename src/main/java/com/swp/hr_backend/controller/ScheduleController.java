package com.swp.hr_backend.controller;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            throws CustomBadRequestException, CustomUnauthorizedException, MessagingException{
        ScheduleDetailResponse scheduleResponses = scheduleService.createSchedule(createScheduleRequest);
        if (scheduleResponses == null) throw new CustomBadRequestException(CustomError.builder().code("403").message("Schedule is null").build());
        return ResponseEntity.ok(scheduleResponses);
    }

    @PostMapping("/update")
    public ResponseEntity<ScheduleDetailResponse> updateSchedule(@RequestBody UpdateScheduleRequest updateScheduleRequest)
            throws CustomBadRequestException, CustomUnauthorizedException, MessagingException{
        ScheduleDetailResponse scheduleDetailResponse = scheduleService.updateSchedule(updateScheduleRequest);
        if (scheduleDetailResponse == null) throw new CustomBadRequestException(CustomError.builder().code("403").message("Schedule is null").build());
        return ResponseEntity.ok(scheduleDetailResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ScheduleDetailResponse> deleteSchedule(@RequestBody DeleteScheduleRequest deleteScheduleRequest)
            throws CustomBadRequestException, CustomUnauthorizedException, MessagingException {
        ScheduleDetailResponse scheduleDetailResponse = scheduleService.deleteSchedule(deleteScheduleRequest);
        if (scheduleDetailResponse == null) throw new CustomBadRequestException(CustomError.builder().code("403").message("Schedule is null").build());
        return ResponseEntity.ok(scheduleDetailResponse);
    }
    @PostMapping(value="/updateStatus")
    public ResponseEntity<String>  updateStatus() {
         return ResponseEntity.ok(scheduleService.loadStatus());
    }
    @GetMapping(value="/getHRemployeeScheduleDetail")
    public ResponseEntity<List<ScheduleDetailResponse>> getHREmployeeDetail() throws CustomUnauthorizedException {
        List<ScheduleDetailResponse> scheduleDetailResponses = scheduleService.viewHREmployeeScheduleDetail();
        return ResponseEntity.ok(scheduleDetailResponses);
    }
    
    
}
