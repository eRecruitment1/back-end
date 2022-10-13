package com.swp.hr_backend.service;

import com.swp.hr_backend.entity.*;
import com.swp.hr_backend.entity.compositeKey.ScheduleDetailID;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.model.request.CreateScheduleRequest;
import com.swp.hr_backend.model.request.UpdateScheduleRequest;
import com.swp.hr_backend.model.response.ScheduleDetailResponse;
import com.swp.hr_backend.repository.*;
import com.swp.hr_backend.utils.AccountRole;
import com.swp.hr_backend.utils.JwtTokenUtil;
import com.swp.hr_backend.utils.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ScheduleServiceImpl implements ScheduleService{

    private final ScheduleRepository scheduleRepository;
    private final ScheduleDetailRepository scheduleDetailRepository;
    private final UserCVRepository userCVRepository;
    private final EmployeeRepository employeeRepository;
    private final FinalResultRepository finalResultRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public List<ScheduleDetailResponse> getSchedule() throws CustomUnauthorizedException, CustomNotFoundException{
        List<ScheduleDetail> scheduleDetails = new ArrayList<>();
        List<ScheduleDetailResponse> scheduleDetailResponses = new ArrayList<>();
        Account acc = jwtTokenUtil.loggedAccount();
        if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.EMPLOYEE)) {
            scheduleDetails = scheduleDetailRepository.findByScheduleDetailIDInterviewerID(acc.getAccountID());
            if (scheduleDetails.size() != 0){
                for (ScheduleDetail s : scheduleDetails) {
                    List<ScheduleDetail> sameSchedule = scheduleDetailRepository.findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(s.getSchedule().getScheduleID(), s.getUserCV().getCvID());
                    List<String> interviewerIDs = new ArrayList<>();
                     for (ScheduleDetail ss : sameSchedule) {
                         interviewerIDs.add(ss.getInterviewer().getAccountID());
                     }
                    scheduleDetailResponses.add(ObjectMapper.scheduleToScheduleDetailResponse(s.getSchedule(), s, interviewerIDs));
                }
                return scheduleDetailResponses;
            } else {
                throw new CustomNotFoundException(CustomError.builder().code("404").message("not found schedule").build());
            }
        } else if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HREMPLOYEE) || jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HRMANAGER)) {
          List<Schedule> sch = scheduleRepository.findAll();
            for (Schedule s : sch
                 ) {
                List<ScheduleDetail> sd = scheduleDetailRepository.findAllBySchedule(s);
                for (ScheduleDetail scd : sd
                     ) {
                    scheduleDetails.add(scd);
                }
            }
            if (scheduleDetails.size() != 0){
                for (ScheduleDetail s : scheduleDetails) {
                    boolean exist = false;
                    List<ScheduleDetail> sameSchedule = scheduleDetailRepository.findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(s.getSchedule().getScheduleID(), s.getUserCV().getCvID());
                    List<String> interviewerIDs = new ArrayList<>();
                    for (ScheduleDetail ss : sameSchedule) {
                        interviewerIDs.add(ss.getInterviewer().getAccountID());
                    }
                    if (scheduleDetailResponses != null)
                        for (ScheduleDetailResponse sdr : scheduleDetailResponses)
                            if (sdr.getCvID() == s.getUserCV().getCvID() && sdr.getScheduleID() == s.getSchedule().getScheduleID()){
                                exist = true;
                                break;
                            }
                    if (!exist)
                        scheduleDetailResponses.add(ObjectMapper.scheduleToScheduleDetailResponse(s.getSchedule(), s, interviewerIDs));
                    }
                return scheduleDetailResponses;
            } else {
                throw new CustomNotFoundException(CustomError.builder().code("404").message("not found schedule").build());
            }
        } else throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
                .message("Access denied, you need to be EMPLOYEE/ HREMPLOYEE/ HRMANAGER to do this!").build());
    }

    @Override
    public ScheduleDetailResponse createSchedule(CreateScheduleRequest createScheduleRequest)
            throws CustomUnauthorizedException, CustomBadRequestException {
        Account acc = jwtTokenUtil.loggedAccount();
        if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HREMPLOYEE) || jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HRMANAGER)) {
            checkValidRequest(createScheduleRequest.getRound(), createScheduleRequest.getCvID(), createScheduleRequest.getInterviewerIDs());
            checkValidCV(createScheduleRequest.getCvID());
            checkValidInterviewer(createScheduleRequest.getInterviewerIDs(), createScheduleRequest.getDate(), createScheduleRequest.getStartTime(), createScheduleRequest.getEndTime());
            Schedule schedule = scheduleRepository.findByDate(createScheduleRequest.getDate());
            if(schedule == null){
                schedule = new Schedule();
                schedule.setDate(createScheduleRequest.getDate());
                schedule = scheduleRepository.save(schedule);
            }
            List<ScheduleDetail> scheduleDetails = new ArrayList<>();
            boolean status = false;
            String urlMeeting = createScheduleRequest.getUrlMeeting();
            Time startTime = createScheduleRequest.getStartTime();
            Time endTime = createScheduleRequest.getEndTime();
            String roundNum = createScheduleRequest.getRound();
            UserCV userCV = userCVRepository.findByCvID(createScheduleRequest.getCvID());

            for (String employeeID : createScheduleRequest.getInterviewerIDs()) {
                ScheduleDetail scheduleDetail = new ScheduleDetail(schedule, userCV, status, startTime, endTime, urlMeeting, roundNum);
//                scheduleDetail.setSchedule(schedule);
//                scheduleDetail.setStatus(false);
//                scheduleDetail.setStartTime();
//                scheduleDetail.setEndTime();
//                scheduleDetail.setUserCV();
//                scheduleDetail.setUrlMeeting();
//                scheduleDetail.setRoundNum();
                scheduleDetail.setInterviewer(employeeRepository.findByAccountID(employeeID));
                scheduleDetail.setScheduleDetailID(new ScheduleDetailID(schedule.getScheduleID(), employeeID, createScheduleRequest.getCvID()));
                scheduleDetails.add(scheduleDetail);
                scheduleDetailRepository.save(scheduleDetail);
            }
            if (scheduleDetails.size()!=0){
                return ObjectMapper.scheduleToScheduleDetailResponse(schedule, new ScheduleDetail(schedule, userCV, status, startTime, endTime, urlMeeting, roundNum), createScheduleRequest.getInterviewerIDs());
            } else throw new CustomBadRequestException(CustomError.builder().code("403").message("Can not create schedule").build());
        } else throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
                .message("Access denied, you need to be HREMPLOYEE/ HRMANAGER to do this!").build());
    }

    public void checkValidRequest(String round, int cvID, List<String> interviewerIDs)
            throws CustomBadRequestException{
        if (userCVRepository.findByCvID(cvID) == null)
            throw new CustomBadRequestException(CustomError.builder().code("403").message("CV is invalid").build());
        FinalResult finalResult = finalResultRepository.findByUserCV(userCVRepository.findByCvID(cvID));
        if (!round.equals(Round.ROUND1.toString())&&!round.equals(Round.ROUND2.toString()))
            throw new CustomBadRequestException(CustomError.builder().code("403").message("Round of schedule is invalid").build());
        if (round.equals(Round.ROUND1.toString())&&!finalResult.getResultStatus().equals(Round.PENDING.toString()))
            throw new CustomBadRequestException(CustomError.builder().code("403").message("Round of schedule is not suited with Round of CV").build());
        if (round.equals(Round.ROUND2.toString())&&!finalResult.getResultStatus().equals(Round.ROUND1.toString()))
            throw new CustomBadRequestException(CustomError.builder().code("403").message("Round of schedule is not suited with Round of CV").build());
        if (round.equals(Round.ROUND1.toString()) && interviewerIDs.size() != 1)
            throw new CustomBadRequestException(CustomError.builder().code("403").message("This round require 1 interviewer (HREMPLOYEE)").build());
        if (round.equals(Round.ROUND2.toString()) && interviewerIDs.size() != 5)
            throw new CustomBadRequestException(CustomError.builder().code("403").message("This round require 5 interviewer (EMPLOYEE)").build());
        for (String interviewerID : interviewerIDs) {
            if (!jwtTokenUtil.checkPermissionAccount(employeeRepository.findByAccountID(interviewerID), AccountRole.HREMPLOYEE) && round.equals(Round.ROUND1.toString()))
                throw new CustomBadRequestException(CustomError.builder().code("403").message("This round require HREMPLOYEE").build());
            else if (!jwtTokenUtil.checkPermissionAccount(employeeRepository.findByAccountID(interviewerID), AccountRole.EMPLOYEE) && round.equals(Round.ROUND2.toString()))
                throw new CustomBadRequestException(CustomError.builder().code("403").message("This round require EMPLOYEE").build());
        }
    }

    public void checkValidInterviewer(List<String> interviewerIDList, Date date, Time startTime, Time endTime) throws CustomBadRequestException {
        List<String> invalidInterviewers = new ArrayList<>();
        for (String interviewerID : interviewerIDList) {
            List<ScheduleDetail> validScheduleDetail = scheduleDetailRepository.findByScheduleDetailIDInterviewerID(interviewerID);
            if (validScheduleDetail != null)
                for (ScheduleDetail scheduleDetail : validScheduleDetail)
                    if (scheduleDetail.getStartTime().toString().equals(startTime.toString()) && scheduleDetail.getEndTime().toString().equals(endTime.toString()))
                        if (scheduleDetail.getSchedule().getDate().toLocalDate().equals(date.toLocalDate()))
                            invalidInterviewers.add(interviewerID);
        }

        if (!invalidInterviewers.isEmpty()) {
            String messageError = "Employee (";
            for (String invalidID : invalidInterviewers) {
                messageError += invalidID + ", ";
            }
            messageError +=  ") can not join this schedule";
            throw new CustomBadRequestException(CustomError.builder().code("403").message(messageError).build());
        }
    }

    public void checkValidCV(int cvID) throws CustomBadRequestException{
        List<ScheduleDetail> scheduleDetails = scheduleDetailRepository.findByScheduleDetailIDCvID(cvID);
        for (ScheduleDetail s : scheduleDetails) {
            if (!s.isStatus())
                throw new CustomBadRequestException(CustomError.builder().code("403").message("This cv is scheduled before").build());
        }
    }

    @Override
    public ScheduleDetailResponse updateSchedule(UpdateScheduleRequest updateScheduleRequest) throws CustomUnauthorizedException, CustomBadRequestException {
        Account acc = jwtTokenUtil.loggedAccount();
        if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HREMPLOYEE) || jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HRMANAGER)) {
            Schedule schedule = scheduleRepository.findByDate(updateScheduleRequest.getDate());
            List<ScheduleDetail> currentSchedule = scheduleDetailRepository.findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(schedule.getScheduleID(), updateScheduleRequest.getCvID());
            List<String> interviewerIDs = new ArrayList<>();
            for (ScheduleDetail sd : currentSchedule){
                interviewerIDs.add(sd.getInterviewer().getAccountID());
            }
            checkValidRequest(updateScheduleRequest.getNewRoundNum(), updateScheduleRequest.getNewCVID(), updateScheduleRequest.getNewInterviewerIDs());

            if(updateScheduleRequest.getNewCVID() != updateScheduleRequest.getCvID())
                checkValidCV(updateScheduleRequest.getNewCVID());
            if ((updateScheduleRequest.getNewInterviewerIDs().size() != interviewerIDs.size()) ||
                    (updateScheduleRequest.getNewInterviewerIDs().size() == interviewerIDs.size() && !updateScheduleRequest.getNewInterviewerIDs().containsAll(interviewerIDs)))
                checkValidInterviewer(updateScheduleRequest.getNewInterviewerIDs(), updateScheduleRequest.getDate(), currentSchedule.get(0).getStartTime(), currentSchedule.get(0).getEndTime());

            if (!currentSchedule.get(0).isStatus()){
                boolean status = false;
                String urlMeeting = currentSchedule.get(0).getUrlMeeting();
                Time startTime = currentSchedule.get(0).getStartTime();
                Time endTime = currentSchedule.get(0).getEndTime();
                String roundNum = updateScheduleRequest.getNewRoundNum();
                if (roundNum == null || roundNum.isEmpty()) roundNum = currentSchedule.get(0).getRoundNum();
                UserCV userCV = userCVRepository.findByCvID(updateScheduleRequest.getNewCVID());
                for (ScheduleDetail s : currentSchedule) {
                    ScheduleDetailID scheduleDetailID = s.getScheduleDetailID();
//                    scheduleDetailRepository.deleteScheduleDetailByScheduleAndAndUserCV(currentSchedule.get(0).getSchedule(), userCV);
//                    scheduleDetailRepository.delete(s);
//                    scheduleDetailRepository.deleteScheduleDetailByScheduleDetailID(scheduleDetailID);
                    scheduleDetailRepository.deleteByScheduleIDAndCVIDAndInterviewerID(s.getScheduleDetailID().getScheduleID(),s.getUserCV().getCvID(),s.getInterviewer().getAccountID());
                }
//                scheduleDetailRepository.deleteByScheduleIDAndCVID(10,3);
                for (String interviewerID : interviewerIDs) {
                    ScheduleDetail scheduleDetail = new ScheduleDetail(schedule, userCV, status, startTime, endTime, urlMeeting, roundNum);
                    scheduleDetail.setInterviewer(employeeRepository.findByAccountID(interviewerID));
                    scheduleDetail.setScheduleDetailID(new ScheduleDetailID(schedule.getScheduleID(), interviewerID, updateScheduleRequest.getNewCVID()));
                    scheduleDetailRepository.save(scheduleDetail);
                }
                return ObjectMapper.scheduleToScheduleDetailResponse(schedule, new ScheduleDetail(schedule, userCV, status, startTime, endTime, urlMeeting, roundNum), updateScheduleRequest.getNewInterviewerIDs());
            } else throw new CustomBadRequestException(CustomError.builder().code("403").message("Schedule has been done, can not update").build());
        } else throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
                .message("Access denied, you need to be HREMPLOYEE/ HRMANAGER to do this!").build());
    }
}
