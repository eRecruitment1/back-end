package com.swp.hr_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.entity.Note;
import com.swp.hr_backend.entity.ScheduleDetail;
import com.swp.hr_backend.entity.UserCV;
import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.request.EvaluateRequest;
import com.swp.hr_backend.repository.EmployeeRepository;
import com.swp.hr_backend.repository.FinalResultRepository;
import com.swp.hr_backend.repository.NoteRepository;
import com.swp.hr_backend.repository.ScheduleDetailRepository;
import com.swp.hr_backend.repository.ScheduleRepository;
import com.swp.hr_backend.repository.UserCVRepository;
import com.swp.hr_backend.utils.AccountRole;
import com.swp.hr_backend.utils.JwtTokenUtil;
import com.swp.hr_backend.utils.ResultStatus;
import com.swp.hr_backend.utils.Round;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserCVImpl implements UserCVService {
	
	private final JwtTokenUtil jwtTokenUtil;
	private final UserCVRepository userCvRepo;
	private final FinalResultRepository finalResultRepo;
	private final NoteRepository noteRepo;
	
	@Override
	public boolean evaluateUserCV(EvaluateRequest evaluate) throws BaseCustomException {
		if (evaluate == null)
			throw new CustomNotFoundException(CustomError.builder().code("400").message("Bad Request").build());
		Account curAcc = jwtTokenUtil.loggedAccount();
		if (curAcc == null)
			throw new CustomUnauthorizedException(CustomError.builder().code("401")
					.message("Access denied, you need to login to do this!").build());
		if (jwtTokenUtil.checkPermissionAccount(curAcc, AccountRole.HREMPLOYEE, AccountRole.HRMANAGER)) {
			UserCV cv = userCvRepo.findByCvID(evaluate.getCvId());
			if (cv != null) {
				if (cv.getFinalResult() == null)
					throw new CustomNotFoundException(
							CustomError.builder().code("404").message("Not Found Final Result!").build());
				String resultStatus = cv.getFinalResult().getResultStatus();
				if (resultStatus.toLowerCase().equals(ResultStatus.NONE.toString().toLowerCase()))
					return false;
				if (resultStatus.toLowerCase().equals(ResultStatus.NOTPASS.toString().toLowerCase()))
					return false;
				if (jwtTokenUtil.checkPermissionAccount(curAcc, AccountRole.HREMPLOYEE)) {
					if (resultStatus.toLowerCase().equals(ResultStatus.PENDING.toString().toLowerCase())) {
						if (!checkAvailableScheduleDetail(cv, Round.ROUND1))
							throw new CustomNotFoundException(
									CustomError.builder().code("403").message("Still on meeting, cannot evaluate cv...").build());
						if (evaluate.isPass()) {
							cv.getFinalResult().setResultStatus(ResultStatus.FIRSTROUND.toString());
						} else {
							cv.getFinalResult().setResultStatus(ResultStatus.NOTPASS.toString());
						}
					} else throw new CustomNotFoundException(
							CustomError.builder().code("403").message("UserCV status isn't at state pending, its through this state!").build());
					if (finalResultRepo.save(cv.getFinalResult()) != null)
						return true;
				}
				if (jwtTokenUtil.checkPermissionAccount(curAcc, AccountRole.HRMANAGER)) {
					if (resultStatus.toLowerCase().equals(ResultStatus.FIRSTROUND.toString().toLowerCase())) {
						if(!checkNumNoteToEvaluate(cv, Round.ROUND2, 6)) 
							throw new CustomNotFoundException(
									CustomError.builder().code("403").message("Not enough note(6 note) to evaluate, cannot evaluate cv...").build());
						if (!checkAvailableScheduleDetail(cv, Round.ROUND2))
							throw new CustomNotFoundException(
									CustomError.builder().code("403").message("Still on meeting, cannot evaluate cv...").build());
						if (evaluate.isPass()) {
							cv.getFinalResult().setResultStatus(ResultStatus.PASS.toString());
						} else {
							cv.getFinalResult().setResultStatus(ResultStatus.NOTPASS.toString());
						}
					} else throw new CustomNotFoundException(
							CustomError.builder().code("403").message("UserCV status isn't at state first round, its through this state!").build());
					if (finalResultRepo.save(cv.getFinalResult()) != null)
						return true;
				}
			}
		} else
			throw new CustomUnauthorizedException(CustomError.builder().code("401")
					.message("Access denied, you need to be Hr Manager or Hr Employee to do this!").build());
		return false;
	}
	
	private boolean checkNumNoteToEvaluate(UserCV cv, Round round, int num) {
		List<Note> list = noteRepo.findNotesByCvId(cv.getCvID());
		if(list != null && list.size() > 0) {
			int count = 0;
			for(Note note : list) {
				if(note.getScheduleDetail().getRoundNum().equalsIgnoreCase(round.toString())) {
					count++;
				}
			}
			if(count == num) return true;
		}
		return false;
	}

	private boolean checkAvailableScheduleDetail(UserCV cv, Round round) {
		List<ScheduleDetail> list = cv.getScheduleDetails();
		if (list == null || (list != null && list.size() <= 0))
			return false;
		boolean finishInterview = true;
		for (ScheduleDetail scheduleDetail : list) {
			if (!scheduleDetail.getRoundNum().toLowerCase().equals(round.toString().toLowerCase()))
				continue;
			if (scheduleDetail.isStatus()) {
				finishInterview = false;
				break;
			}
		}
		return finishInterview;
	}
}
