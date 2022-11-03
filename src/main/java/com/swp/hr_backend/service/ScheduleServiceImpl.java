package com.swp.hr_backend.service;

import com.swp.hr_backend.entity.*;
import com.swp.hr_backend.entity.compositeKey.ScheduleDetailID;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.model.request.CreateScheduleRequest;
import com.swp.hr_backend.model.request.DataMailRequest;
import com.swp.hr_backend.model.request.DeleteScheduleRequest;
import com.swp.hr_backend.model.request.UpdateScheduleRequest;
import com.swp.hr_backend.model.response.AccountResponse;
import com.swp.hr_backend.model.response.ScheduleDetailResponse;
import com.swp.hr_backend.repository.*;
import com.swp.hr_backend.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleDetailRepository scheduleDetailRepository;
    private final UserCVRepository userCVRepository;
    private final EmployeeRepository employeeRepository;
    private final FinalResultRepository finalResultRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final MailService mailService;
    private final AccountRepository accountRepository;
    private final RoomRepository roomRepository;

    @Override
    public List<ScheduleDetailResponse> getSchedule() throws CustomUnauthorizedException, CustomNotFoundException {
        loadStatusSchedule();
        List<ScheduleDetail> scheduleDetails = new ArrayList<>();
        List<ScheduleDetailResponse> scheduleDetailResponses = new ArrayList<>();
        Account acc = jwtTokenUtil.loggedAccount();
        if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.EMPLOYEE)) {
            scheduleDetails = scheduleDetailRepository.findByScheduleDetailIDInterviewerID(acc.getAccountID());
            if (scheduleDetails.size() != 0) {
                for (ScheduleDetail s : scheduleDetails) {
                    List<ScheduleDetail> sameSchedule = scheduleDetailRepository
                            .findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(s.getSchedule().getScheduleID(),
                                    s.getUserCV().getCvID());
                    List<AccountResponse> accountResponses = new ArrayList<>();
                    List<String> interviewerIDs = new ArrayList<>();
                    for (ScheduleDetail ss : sameSchedule) {
                        Account account = accountRepository.findById(ss.getInterviewer().getAccountID()).get();
                        accountResponses.add(ObjectMapper.accountToAccountResponse(account));
                        interviewerIDs.add(ss.getInterviewer().getAccountID());
                    }
                    ScheduleDetailResponse scheduleDetailResponse = ObjectMapper.scheduleToScheduleDetailResponse(s.getSchedule(), s, interviewerIDs);
                    scheduleDetailResponse.setAccountResponses(accountResponses);
                    scheduleDetailResponses
                            .add(scheduleDetailResponse);
                }
                return scheduleDetailResponses;
            } else {
                throw new CustomNotFoundException(
                        CustomError.builder().code("404").message("not found schedule").build());
            }
        } else if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HREMPLOYEE)
                || jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HRMANAGER)) {
            List<Schedule> sch = scheduleRepository.findAll();
            for (Schedule s : sch) {
                List<ScheduleDetail> sd = scheduleDetailRepository.findAllBySchedule(s);
                for (ScheduleDetail scd : sd) {
                    scheduleDetails.add(scd);
                }
            }
            if (scheduleDetails.size() != 0) {
                for (ScheduleDetail s : scheduleDetails) {
                    boolean exist = false;
                    List<ScheduleDetail> sameSchedule = scheduleDetailRepository
                            .findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(s.getSchedule().getScheduleID(),
                                    s.getUserCV().getCvID());
                    List<String> interviewerIDs = new ArrayList<>();
                    List<AccountResponse> accountResponses = new ArrayList<>();
                    for (ScheduleDetail ss : sameSchedule) {
                        Account account = accountRepository.findById(ss.getInterviewer().getAccountID()).get();
                        accountResponses.add(ObjectMapper.accountToAccountResponse(account));
                        interviewerIDs.add(ss.getInterviewer().getAccountID());
                    }
                    ScheduleDetailResponse scheduleDetailResponse = ObjectMapper.scheduleToScheduleDetailResponse(s.getSchedule(), s, interviewerIDs);
                    scheduleDetailResponse.setAccountResponses(accountResponses);
                    if (scheduleDetailResponses != null)
                        for (ScheduleDetailResponse sdr : scheduleDetailResponses)
                            if (sdr.getCvID() == s.getUserCV().getCvID()
                                    && sdr.getScheduleID() == s.getSchedule().getScheduleID()) {
                                exist = true;
                                break;
                            }
                    if (!exist)
                        scheduleDetailResponses
                                .add(scheduleDetailResponse);
                }
                return scheduleDetailResponses;
            } else {
                throw new CustomNotFoundException(
                        CustomError.builder().code("404").message("not found schedule").build());
            }
        } else
            throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
                    .message("Access denied, you need to be EMPLOYEE/ HREMPLOYEE/ HRMANAGER to do this!").build());
    }

    @Override
    public ScheduleDetailResponse createSchedule(CreateScheduleRequest createScheduleRequest)
            throws CustomUnauthorizedException, CustomBadRequestException, MessagingException {
        loadStatusSchedule();
        Account acc = jwtTokenUtil.loggedAccount();
        if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HREMPLOYEE)
                || jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HRMANAGER)) {
            String urlMeeting = createScheduleRequest.getUrlMeeting();
            Time startTime = createScheduleRequest.getStartTime();
            Time endTime = createScheduleRequest.getEndTime();
            String roundNum = createScheduleRequest.getRound();
            UserCV userCV = userCVRepository.findByCvID(createScheduleRequest.getCvID());
            Room room = roomRepository.findByRoomName(createScheduleRequest.getRoomName());
            checkValidRequest(roundNum, createScheduleRequest.getCvID(), createScheduleRequest.getInterviewerIDs());
            checkValidTime(createScheduleRequest.getDate(), startTime, endTime);
            checkValidCV(createScheduleRequest.getCvID());
            checkValidInterviewer(createScheduleRequest.getInterviewerIDs(), createScheduleRequest.getDate(),
                    startTime, endTime);
            checkValidUrlAndRoom(urlMeeting, room, createScheduleRequest.getDate(), startTime, endTime);
            Schedule schedule = scheduleRepository.findByDate(createScheduleRequest.getDate());
            if (schedule == null) {
                schedule = new Schedule();
                schedule.setDate(createScheduleRequest.getDate());
                schedule = scheduleRepository.save(schedule);
            }
            List<ScheduleDetail> scheduleDetails = new ArrayList<>();
            boolean status = false;
            for (String employeeID : createScheduleRequest.getInterviewerIDs()) {
                ScheduleDetail scheduleDetail = new ScheduleDetail(schedule, userCV, status, startTime, endTime,
                        urlMeeting, roundNum, room);
                scheduleDetail.setInterviewer(employeeRepository.findByAccountID(employeeID));
                scheduleDetail.setScheduleDetailID(
                        new ScheduleDetailID(schedule.getScheduleID(), employeeID, createScheduleRequest.getCvID()));
                scheduleDetails.add(scheduleDetail);
                scheduleDetailRepository.save(scheduleDetail);
            }
            if (scheduleDetails.size() != 0) {
                DataMailRequest dataMailRequest = new DataMailRequest();
                dataMailRequest.setSubject(MailSubjectConstant.INTERVIEWER_SCHEDULE);
                for (ScheduleDetail sd : scheduleDetails) {
                    if (sd.getInterviewer().isEnabled()) {
                        dataMailRequest.setTo(sd.getInterviewer().getEmail());
                        mailService.sendHtmlMail(dataMailRequest,
                                MailBody.interviewSchedule(sd.getInterviewer().getFirstname(),
                                        sd.getSchedule().getDate(), startTime, endTime,
                                        urlMeeting, roundNum, createScheduleRequest.getRoomName()));
                    }
                }
                if (userCV.getCandidate().isEnabled()) {
                    dataMailRequest.setSubject(MailSubjectConstant.CANDIDATE_SCHEDULE);
                    dataMailRequest.setTo(userCV.getCandidate().getEmail());
                    mailService.sendHtmlMail(dataMailRequest,
                            MailBody.candidateSchedule(userCV.getCandidate().getFirstname(),
                                    createScheduleRequest.getDate(), startTime, endTime, urlMeeting, roundNum
                                    , createScheduleRequest.getRoomName()));
                }
                return ObjectMapper.scheduleToScheduleDetailResponse(schedule,
                        new ScheduleDetail(schedule, userCV, status, startTime, endTime, urlMeeting, roundNum, room),
                        createScheduleRequest.getInterviewerIDs());
            } else
                throw new CustomBadRequestException(
                        CustomError.builder().code("403").message("Can not create schedule").build());
        } else
            throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
                    .message("Access denied, you need to be HREMPLOYEE/ HRMANAGER to do this!").build());
    }

    public void checkValidUrlAndRoom(String urlMeeting, Room room, Date date, Time startTime, Time endTime) throws CustomBadRequestException{
        if (room == null && urlMeeting.isEmpty())
            throw new CustomBadRequestException(CustomError.builder().code("403")
                    .message("Both room and url meeting are empty").build());
        if (room != null && !urlMeeting.isEmpty())
            throw new CustomBadRequestException(CustomError.builder().code("403")
                    .message("Just only choose 1 (urlMeeting or room)").build());
        ScheduleDetail scheduleDetail;
        Schedule schedule;
        if (room != null) {
            scheduleDetail = scheduleDetailRepository.findByNoAndStatusAndStartTimeAndEndTime(room.getNo(), false, startTime, endTime);
            if (scheduleDetail != null) {
                schedule = scheduleRepository.findByScheduleID(scheduleDetail.getScheduleDetailID().getScheduleID());
                if (schedule.getDate().toString().equals(date.toString()))
                    throw new CustomBadRequestException(CustomError.builder().code("403")
                            .message("Room is invalid this time").build());
            }
        }
        if (urlMeeting != null) {
            scheduleDetail = scheduleDetailRepository.findByUrlMeetingAndStatusAndStartTimeAndEndTime(urlMeeting, false, startTime, endTime);
            if (scheduleDetail != null) {
                schedule = scheduleRepository.findByScheduleID(scheduleDetail.getScheduleDetailID().getScheduleID());
                if (schedule.getDate().toString().equals(date.toString()))
                    throw new CustomBadRequestException(CustomError.builder().code("403")
                            .message("Url is invalid this time").build());
            }
        }
    }

    public void checkValidRequest(String round, int cvID, List<String> interviewerIDs)
            throws CustomBadRequestException {
        if (userCVRepository.findByCvID(cvID) == null)
            throw new CustomBadRequestException(CustomError.builder().code("403").message("CV is invalid").build());
        FinalResult finalResult = finalResultRepository.findByUserCV(userCVRepository.findByCvID(cvID));
        if (!round.equals(Round.ROUND1.toString()) && !round.equals(Round.ROUND2.toString()))
            throw new CustomBadRequestException(
                    CustomError.builder().code("403").message("Round of schedule is invalid").build());
        if (round.equals(Round.ROUND1.toString()) && !finalResult.getResultStatus().equals(Round.PENDING.toString()))
            throw new CustomBadRequestException(CustomError.builder().code("403")
                    .message("Round of schedule is not suited with Round of CV").build());
        if (round.equals(Round.ROUND2.toString()) && !finalResult.getResultStatus().equals(Round.ROUND1.toString()))
            throw new CustomBadRequestException(CustomError.builder().code("403")
                    .message("Round of schedule is not suited with Round of CV").build());
        if (scheduleDetailRepository.findByScheduleDetailIDCvIDAndRoundNum(cvID, round).size()!=0)
            throw new CustomBadRequestException(CustomError.builder().code("403")
                    .message("This CV has been scheduled for this round before").build());
        if (round.equals(Round.ROUND1.toString()) && interviewerIDs.size() != 1)
            throw new CustomBadRequestException(
                    CustomError.builder().code("403").message("This round require 1 interviewer (HREMPLOYEE)").build());
        if (round.equals(Round.ROUND2.toString()) && interviewerIDs.size() != 5)
            throw new CustomBadRequestException(
                    CustomError.builder().code("403").message("This round require 5 interviewer (EMPLOYEE)").build());
        for (String interviewerID : interviewerIDs) {
            if (!jwtTokenUtil.checkPermissionAccount(employeeRepository.findByAccountID(interviewerID),
                    AccountRole.HREMPLOYEE) && round.equals(Round.ROUND1.toString()))
                throw new CustomBadRequestException(
                        CustomError.builder().code("403").message("This round require HREMPLOYEE").build());
            else if (!jwtTokenUtil.checkPermissionAccount(employeeRepository.findByAccountID(interviewerID),
                    AccountRole.EMPLOYEE) && round.equals(Round.ROUND2.toString()))
                throw new CustomBadRequestException(
                        CustomError.builder().code("403").message("This round require EMPLOYEE").build());
        }
    }

    public void checkValidInterviewer(List<String> interviewerIDList, Date date, Time startTime, Time endTime)
            throws CustomBadRequestException {
        List<String> invalidInterviewers = new ArrayList<>();
        for (String interviewerID : interviewerIDList) {
            List<ScheduleDetail> validScheduleDetail = scheduleDetailRepository
                    .findByScheduleDetailIDInterviewerID(interviewerID);
            if (validScheduleDetail != null)
                for (ScheduleDetail scheduleDetail : validScheduleDetail)
                    if (scheduleDetail.getStartTime().toString().equals(startTime.toString())
                            && scheduleDetail.getEndTime().toString().equals(endTime.toString()))
                        if (scheduleDetail.getSchedule().getDate().toLocalDate().equals(date.toLocalDate()))
                            invalidInterviewers.add(interviewerID);
        }

        if (!invalidInterviewers.isEmpty()) {
            String messageError = "Employee (";
            for (String invalidID : invalidInterviewers) {
                messageError += invalidID + ", ";
            }
            messageError += ") can not join this schedule";
            throw new CustomBadRequestException(CustomError.builder().code("403").message(messageError).build());
        }
    }

    public void checkValidCV(int cvID) throws CustomBadRequestException {
        List<ScheduleDetail> scheduleDetails = scheduleDetailRepository.findByScheduleDetailIDCvID(cvID);
        for (ScheduleDetail s : scheduleDetails) {
            if (!s.isStatus())
                throw new CustomBadRequestException(
                        CustomError.builder().code("403").message("This cv is scheduled before").build());
        }
    }

    public void checkValidTime(Date date, Time startTime, Time endTime) throws CustomBadRequestException {
        LocalDateTime end = LocalDateTime.parse(date.toString() + "T" + endTime.toString());
        LocalDateTime start = LocalDateTime.parse(date.toString() + "T" + startTime.toString());
        long duration = Duration.between(start, end).toMinutes();
        if (start.isAfter(end)) throw new CustomBadRequestException(CustomError.builder().code("403")
                .message("Time is invalid").build());
        if (end.isBefore(LocalDateTime.now())) throw new CustomBadRequestException(CustomError.builder().code("403")
                .message("Schedule must take place in the future").build());
        if (duration < 45) throw new CustomBadRequestException(CustomError.builder().code("403")
                .message("Time is not enough to create a meeting").build());
    }

    @Override
    public ScheduleDetailResponse updateSchedule(UpdateScheduleRequest updateScheduleRequest)
            throws CustomUnauthorizedException, CustomBadRequestException, MessagingException {
        loadStatusSchedule();
        Account acc = jwtTokenUtil.loggedAccount();
        if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HREMPLOYEE)
                || jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HRMANAGER)) {
            Schedule schedule = scheduleRepository.findByDate(updateScheduleRequest.getDate());
            List<ScheduleDetail> currentSchedule = scheduleDetailRepository
                    .findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(schedule.getScheduleID(),
                            updateScheduleRequest.getCvID());
            List<String> interviewerIDs = new ArrayList<>();
            for (ScheduleDetail sd : currentSchedule) {
                interviewerIDs.add(sd.getInterviewer().getAccountID());
            }
            checkValidRequest(updateScheduleRequest.getNewRoundNum(), updateScheduleRequest.getNewCVID(),
                    updateScheduleRequest.getNewInterviewerIDs());

            if (updateScheduleRequest.getNewCVID() != updateScheduleRequest.getCvID())
                checkValidCV(updateScheduleRequest.getNewCVID());
            if ((updateScheduleRequest.getNewInterviewerIDs().size() != interviewerIDs.size()) ||
                    (updateScheduleRequest.getNewInterviewerIDs().size() == interviewerIDs.size()
                            && !updateScheduleRequest.getNewInterviewerIDs().containsAll(interviewerIDs)))
                checkValidInterviewer(updateScheduleRequest.getNewInterviewerIDs(), updateScheduleRequest.getDate(),
                        currentSchedule.get(0).getStartTime(), currentSchedule.get(0).getEndTime());

            if (!currentSchedule.get(0).isStatus()) {
                boolean status = false;
                String urlMeeting = currentSchedule.get(0).getUrlMeeting();
                Time startTime = currentSchedule.get(0).getStartTime();
                Time endTime = currentSchedule.get(0).getEndTime();
                String roundNum = updateScheduleRequest.getNewRoundNum();
                Room room = currentSchedule.get(0).getRoom();
                if (roundNum == null || roundNum.isEmpty())
                    roundNum = currentSchedule.get(0).getRoundNum();
                UserCV userCV = userCVRepository.findByCvID(updateScheduleRequest.getNewCVID());
                for (ScheduleDetail s : currentSchedule) {
                    scheduleDetailRepository.deleteByScheduleIDAndCVIDAndInterviewerID(
                            s.getScheduleDetailID().getScheduleID(), s.getUserCV().getCvID(),
                            s.getInterviewer().getAccountID());
                }
                for (String interviewerID : updateScheduleRequest.getNewInterviewerIDs()) {
                    ScheduleDetail scheduleDetail = new ScheduleDetail(schedule, userCV, status, startTime, endTime,
                            urlMeeting, roundNum, room);
                    scheduleDetail.setInterviewer(employeeRepository.findByAccountID(interviewerID));
                    scheduleDetail.setScheduleDetailID(new ScheduleDetailID(schedule.getScheduleID(), interviewerID,
                            updateScheduleRequest.getNewCVID()));
                    scheduleDetail = scheduleDetailRepository.save(scheduleDetail);
                    if (scheduleDetail == null)
                        return null;
                }
                sendMailForUpdateSchedule(updateScheduleRequest, interviewerIDs, currentSchedule.get(0).getRoundNum());
                return ObjectMapper.scheduleToScheduleDetailResponse(schedule,
                        new ScheduleDetail(schedule, userCV, status, startTime, endTime, urlMeeting, roundNum, room),
                        updateScheduleRequest.getNewInterviewerIDs());
            } else
                throw new CustomBadRequestException(
                        CustomError.builder().code("403").message("Schedule has been done, can not update").build());
        } else
            throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
                    .message("Access denied, you need to be HREMPLOYEE/ HRMANAGER to do this!").build());
    }

    @Override
    public ScheduleDetailResponse deleteSchedule(DeleteScheduleRequest deleteScheduleRequest)
            throws CustomUnauthorizedException, CustomBadRequestException, MessagingException {
        loadStatusSchedule();
        Account acc = jwtTokenUtil.loggedAccount();
        if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HREMPLOYEE)
                || jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HRMANAGER)) {
            List<ScheduleDetail> scheduleDetail = scheduleDetailRepository
                    .findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(deleteScheduleRequest.getScheduleID(),
                            deleteScheduleRequest.getCvID());
            if (scheduleDetail == null)
                throw new CustomBadRequestException(
                        CustomError.builder().code("403").message("Schedule is not exist to delete").build());
            List<String> interviewerIDs = new ArrayList<>();
            Date date = scheduleDetail.get(0).getSchedule().getDate();
            Time startTime = scheduleDetail.get(0).getStartTime();
            Time endTime = scheduleDetail.get(0).getEndTime();
            if (scheduleDetail.get(0).isStatus())
                throw new CustomBadRequestException(
                        CustomError.builder().code("403").message("Schedule has been done, cannot delete").build());
            for (ScheduleDetail sd : scheduleDetail) {
                interviewerIDs.add(sd.getInterviewer().getAccountID());
                scheduleDetailRepository.deleteByScheduleIDAndCVIDAndInterviewerID(
                        deleteScheduleRequest.getScheduleID(), deleteScheduleRequest.getCvID(),
                        sd.getInterviewer().getAccountID());
            }
            DataMailRequest dataMailRequest = new DataMailRequest();
            dataMailRequest.setSubject(MailSubjectConstant.DELETE_SCHEDULE);
            for (String i : interviewerIDs) {
                Employee e = employeeRepository.findByAccountID(i);
                if (e.isEnabled()) {
                    dataMailRequest.setTo(e.getEmail());
                    mailService.sendHtmlMail(dataMailRequest,
                            MailBody.mailDeleteSchedule(e.getFirstname(), date, startTime, endTime, ""));
                }
            }
            if (userCVRepository.findByCvID(deleteScheduleRequest.getCvID()).getCandidate().isEnabled()) {
                dataMailRequest.setSubject(MailSubjectConstant.CANDIDATE_SCHEDULE);
                dataMailRequest
                        .setTo(userCVRepository.findByCvID(deleteScheduleRequest.getCvID()).getCandidate().getEmail());
                mailService.sendHtmlMail(dataMailRequest, MailBody.mailDeleteSchedule(
                        userCVRepository.findByCvID(deleteScheduleRequest.getCvID()).getCandidate().getFirstname(),
                        date, startTime, endTime, "candidate"));
            }
            return ObjectMapper.scheduleToScheduleDetailResponse(scheduleDetail.get(0).getSchedule(),
                    scheduleDetail.get(0), interviewerIDs);
        } else
            throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
                    .message("Access denied, you need to be HREMPLOYEE/ HRMANAGER to do this!").build());
    }

    public void loadStatusSchedule() {
        Iterable<ScheduleDetail> all = scheduleDetailRepository.findAll();
        for (ScheduleDetail scheduleDetail : all) {
            Date date = scheduleDetail.getSchedule().getDate();
            Time endTime = scheduleDetail.getEndTime();
            String scheTime = date.toString() + "T" + endTime.toString();
            LocalDateTime scheDate = LocalDateTime.parse(scheTime);
            if (scheDate.isAfter(LocalDateTime.now())) {
                scheduleDetail.setStatus(false);
                scheduleDetailRepository.save(scheduleDetail);
            } else
                scheduleDetail.setStatus(true);
        }
    }

    public List<String> getUpdatedInterviewer(UpdateScheduleRequest updateScheduleRequest,
            List<String> oldInterviewerIDs) {
        List<String> changedInterviewer = new ArrayList<>();
        for (String id : updateScheduleRequest.getNewInterviewerIDs()) {
            if (!oldInterviewerIDs.contains(id))
                changedInterviewer.add(id);
        }
        return changedInterviewer;
    }

    public List<String> getDeletedInterviewer(UpdateScheduleRequest updateScheduleRequest,
            List<String> oldInterviewerIDs) {
        List<String> deletedInterviewer = new ArrayList<>();
        for (String id : oldInterviewerIDs) {
            if (!updateScheduleRequest.getNewInterviewerIDs().contains(id))
                deletedInterviewer.add(id);
        }
        return deletedInterviewer;
    }

    public void sendMailForUpdateSchedule(UpdateScheduleRequest updateScheduleRequest, List<String> oldInterviewerIDs,
                                          String oldRound) throws MessagingException {
        UserCV newCV = userCVRepository.findByCvID(updateScheduleRequest.getNewCVID());
        UserCV oldCV = userCVRepository.findByCvID(updateScheduleRequest.getCvID());
        List<ScheduleDetail> scheduleDetails = scheduleDetailRepository
                .findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(
                        scheduleRepository.findByDate(updateScheduleRequest.getDate()).getScheduleID(),
                        oldCV.getCvID());
        List<String> changedInterviewer = getUpdatedInterviewer(updateScheduleRequest, oldInterviewerIDs);
        List<String> deletedInterviewer = getDeletedInterviewer(updateScheduleRequest, oldInterviewerIDs);
        DataMailRequest dataMailRequest = new DataMailRequest();
        String roomName = "";
        if (scheduleDetails.get(0).getRoom()!=null) roomName =scheduleDetails.get(0).getRoom().getRoomName();
        if (updateScheduleRequest.getNewRoundNum().equals(oldRound)) {
            if (updateScheduleRequest.getNewCVID() != updateScheduleRequest.getNewCVID()) {
                if (oldCV.getCandidate().isEnabled()) {
                    dataMailRequest.setSubject(MailSubjectConstant.DELETE_SCHEDULE);
                    dataMailRequest.setTo(oldCV.getCandidate().getEmail());
                    mailService.sendHtmlMail(dataMailRequest,
                            MailBody.mailDeleteSchedule(oldCV.getCandidate().getFirstname(),
                                    updateScheduleRequest.getDate(), scheduleDetails.get(0).getStartTime(),
                                    scheduleDetails.get(0).getEndTime(), "candidate"));
                }
                if (newCV.getCandidate().isEnabled()) {
                    dataMailRequest.setSubject(MailSubjectConstant.CANDIDATE_SCHEDULE);
                    dataMailRequest.setTo(newCV.getCandidate().getEmail());
                    mailService.sendHtmlMail(dataMailRequest,
                            MailBody.candidateSchedule(newCV.getCandidate().getFirstname(),
                                    updateScheduleRequest.getDate(), scheduleDetails.get(0).getStartTime(),
                                    scheduleDetails.get(0).getEndTime(), scheduleDetails.get(0).getUrlMeeting(),
                                    oldRound, roomName));
                }
            }
            if (changedInterviewer.size() != 0) {
                dataMailRequest.setSubject(MailSubjectConstant.DELETE_SCHEDULE);
                for (String di : deletedInterviewer) {
                    if (employeeRepository.findByAccountID(di).isEnabled()) {
                        dataMailRequest.setTo(employeeRepository.findByAccountID(di).getEmail());
                        mailService.sendHtmlMail(dataMailRequest,
                                MailBody.mailDeleteSchedule(employeeRepository.findByAccountID(di).getFirstname(),
                                        updateScheduleRequest.getDate(), scheduleDetails.get(0).getStartTime(),
                                        scheduleDetails.get(0).getEndTime(), ""));
                    }
                }
                dataMailRequest.setSubject(MailSubjectConstant.INTERVIEWER_SCHEDULE);
                for (String ci : changedInterviewer) {
                    if (employeeRepository.findByAccountID(ci).isEnabled()) {
                        dataMailRequest.setTo(employeeRepository.findByAccountID(ci).getEmail());
                        mailService.sendHtmlMail(dataMailRequest,
                                MailBody.interviewSchedule(employeeRepository.findByAccountID(ci).getFirstname(),
                                        updateScheduleRequest.getDate(), scheduleDetails.get(0).getStartTime(),
                                        scheduleDetails.get(0).getEndTime(), scheduleDetails.get(0).getUrlMeeting(),
                                        oldRound, roomName));
                    }
                }
            }
        } else {
            if (oldCV.getCandidate().isEnabled()) {
                dataMailRequest.setSubject(MailSubjectConstant.DELETE_SCHEDULE);
                dataMailRequest.setTo(oldCV.getCandidate().getEmail());
                mailService.sendHtmlMail(dataMailRequest,
                        MailBody.mailDeleteSchedule(oldCV.getCandidate().getFirstname(),
                                updateScheduleRequest.getDate(), scheduleDetails.get(0).getStartTime(),
                                scheduleDetails.get(0).getEndTime(), "candidate"));
            }
            for (String di : oldInterviewerIDs) {
                if (employeeRepository.findByAccountID(di).isEnabled()) {
                    dataMailRequest.setTo(employeeRepository.findByAccountID(di).getEmail());
                    mailService.sendHtmlMail(dataMailRequest,
                            MailBody.mailDeleteSchedule(employeeRepository.findByAccountID(di).getFirstname(),
                                    updateScheduleRequest.getDate(), scheduleDetails.get(0).getStartTime(),
                                    scheduleDetails.get(0).getEndTime(), ""));
                }
            }
            dataMailRequest.setSubject(MailSubjectConstant.INTERVIEWER_SCHEDULE);
            for (String ci : updateScheduleRequest.getNewInterviewerIDs()) {
                if (employeeRepository.findByAccountID(ci).isEnabled()) {
                    dataMailRequest.setTo(employeeRepository.findByAccountID(ci).getEmail());
                    mailService.sendHtmlMail(dataMailRequest,
                            MailBody.interviewSchedule(employeeRepository.findByAccountID(ci).getFirstname(),
                                    updateScheduleRequest.getDate(), scheduleDetails.get(0).getStartTime(),
                                    scheduleDetails.get(0).getEndTime(), scheduleDetails.get(0).getUrlMeeting(),
                                    updateScheduleRequest.getNewRoundNum(), roomName));
                }
            }
            if (newCV.getCandidate().isEnabled()) {
                dataMailRequest.setSubject(MailSubjectConstant.CANDIDATE_SCHEDULE);
                dataMailRequest.setTo(newCV.getCandidate().getEmail());
                mailService.sendHtmlMail(dataMailRequest,
                        MailBody.candidateSchedule(newCV.getCandidate().getFirstname(), updateScheduleRequest.getDate(),
                                scheduleDetails.get(0).getStartTime(), scheduleDetails.get(0).getEndTime(),
                                scheduleDetails.get(0).getUrlMeeting(), updateScheduleRequest.getNewRoundNum(), roomName));
            }
        }
    }

    @Override
    public String loadStatus() {
        Iterable<ScheduleDetail> all = scheduleDetailRepository.findAll();
        for (ScheduleDetail scheduleDetail : all) {
            Date date = scheduleDetail.getSchedule().getDate();
            Time endTime = scheduleDetail.getEndTime();
            String scheTime = date.toString() + "T" + endTime.toString();
            LocalDateTime scheDate = LocalDateTime.parse(scheTime);
            if (scheDate.isAfter(LocalDateTime.now())) {
                scheduleDetail.setStatus(false);
                scheduleDetailRepository.save(scheduleDetail);
            } else
                scheduleDetail.setStatus(true);
        }
        return "successful";
    }

    @Override
    public List<ScheduleDetailResponse> viewHREmployeeScheduleDetail() throws CustomUnauthorizedException {
        Account account = jwtTokenUtil.loggedAccount();
        List<ScheduleDetail> scheduleDetails = new ArrayList<>();
        List<ScheduleDetailResponse> scheduleDetailResponses = new ArrayList<>();
        if (jwtTokenUtil.checkPermissionAccount(account, AccountRole.HREMPLOYEE)) {
            scheduleDetails = scheduleDetailRepository.findByScheduleDetailIDInterviewerID(account.getAccountID());
            ;
            for (ScheduleDetail s : scheduleDetails) {
                if (s.getRoundNum().equalsIgnoreCase("Round1")
                        && s.getInterviewer().getAccountID().equalsIgnoreCase(account.getAccountID())) {
                    List<ScheduleDetail> sameSchedule = scheduleDetailRepository
                            .findByScheduleDetailIDScheduleIDAndScheduleDetailIDCvID(s.getSchedule().getScheduleID(),
                                    s.getUserCV().getCvID());
                    List<String> interviewerIDs = new ArrayList<>();
                    for (ScheduleDetail ss : sameSchedule) {
                        interviewerIDs.add(ss.getInterviewer().getAccountID());
                    }
                    scheduleDetailResponses
                            .add(ObjectMapper.scheduleToScheduleDetailResponse(s.getSchedule(), s, interviewerIDs));
                }
            }
            return scheduleDetailResponses;
        } else {
            throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
                    .message("Access denied, you need to be HREMPLOYEE to do this!").build());
        }
    }

}
