package com.swp.hr_backend.service;

import java.util.ArrayList;
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
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.model.request.EvaluateRequest;
import com.swp.hr_backend.model.request.NoteRequest;
import com.swp.hr_backend.model.request.ViewNoteRequest;
import com.swp.hr_backend.model.response.NoteResponse;
import com.swp.hr_backend.repository.AccountRepository;
import com.swp.hr_backend.repository.CandidateRepository;
import com.swp.hr_backend.repository.FinalResultRepository;
import com.swp.hr_backend.repository.NoteRepository;
import com.swp.hr_backend.repository.ScheduleDetailRepository;
import com.swp.hr_backend.repository.UserCVRepository;
import com.swp.hr_backend.utils.AccountRole;
import com.swp.hr_backend.utils.JwtTokenUtil;
import com.swp.hr_backend.utils.ResultStatus;
import com.swp.hr_backend.utils.Round;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

	private final JwtTokenUtil jwtTokenUtil;
	private final NoteRepository noteRepo;
	private final ScheduleDetailRepository scheduleDetailRepo;
	private final UserCVRepository userCvRepo;
	private final FinalResultRepository finalResultRepo;

	@Override
	public NoteResponse takeNote(NoteRequest noteReq) throws BaseCustomException {
		if (noteReq == null)
			throw new CustomNotFoundException(CustomError.builder().code("400").message("Bad Request").build());
		Account curAcc = jwtTokenUtil.loggedAccount();
		if (curAcc == null)
			throw new CustomUnauthorizedException(
					CustomError.builder().code("401").message("Access denied, you need to login to do this!").build());
		if (jwtTokenUtil.checkPermissionAccount(curAcc, AccountRole.EMPLOYEE, AccountRole.HREMPLOYEE)) {
			System.out.println(noteReq.getScheduleId() + ", " + curAcc.getAccountID() + ", " + noteReq.getCvId());
			ScheduleDetail scheduleDetail = scheduleDetailRepo.findByScheduleIDAndAccountIDAndCvId(
					noteReq.getScheduleId(), curAcc.getAccountID(), noteReq.getCvId());
			if (scheduleDetail != null) {
				if (scheduleDetail.isStatus()) {
					scheduleDetail.setStatus(false);
					if(scheduleDetailRepo.save(scheduleDetail) == null) 
						throw new CustomNotFoundException(
							CustomError.builder().code("404").message("Not Found Schedule Detail!").build());
				}
				Note note = scheduleDetail.getNote();
				if (note != null) {
					note.setMessage(noteReq.getMessage());
					note.setPoint(noteReq.getPoint());
					return ObjectMapper.noteToNoteResponse(noteRepo.save(note));
				} else {
					Note nNote = ObjectMapper.noteRequestToNote(noteReq, scheduleDetail);
					if (nNote != null)
						return ObjectMapper.noteToNoteResponse(noteRepo.save(nNote));
				}
			} else
				throw new CustomNotFoundException(
						CustomError.builder().code("404").message("Not Found Schedule Detail!").build());
		} else
			throw new CustomUnauthorizedException(CustomError.builder().code("401")
					.message("Access denied, you need to be Employee or HrEmployee to do this!").build());
		return null;
	}

	@Override
	public List<NoteResponse> viewNotes() throws BaseCustomException {
		if (jwtTokenUtil.checkPermissionCurrentAccount(AccountRole.HRMANAGER)) {
			return ObjectMapper.notesToNoteResponses(noteRepo.findAll());
		} else
			throw new CustomUnauthorizedException(CustomError.builder().code("401")
					.message("Access denied, you need to be Hr Manager to do this!").build());
	}

	@Override
	public List<NoteResponse> viewNotesByCvIdAndRoundNum(ViewNoteRequest viewNoteReq) throws BaseCustomException {
		if (viewNoteReq == null)
			throw new CustomNotFoundException(CustomError.builder().code("400").message("Bad Request").build());
		if (jwtTokenUtil.checkPermissionCurrentAccount(AccountRole.HRMANAGER)) {
			List<Note> listAllNoteOfCv = noteRepo.findNotesByCvId(viewNoteReq.getCvId());
			if (listAllNoteOfCv != null && listAllNoteOfCv.size() > 0) {
				List<Note> listAllNoteOfCvAndRoundNum = new ArrayList<Note>();
				if (viewNoteReq.getRoundNum() > 3 || viewNoteReq.getRoundNum() < 1)
					throw new CustomNotFoundException(CustomError.builder().code("400").message("Bad Request").build());
				System.out.println(Round.valueOf(viewNoteReq.getRoundNum()));
				for (Note note : listAllNoteOfCv) {
					if (note.getScheduleDetail().getRoundNum().toLowerCase()
							.equals(Round.valueOf(viewNoteReq.getRoundNum()).toString().toLowerCase())) {
						listAllNoteOfCvAndRoundNum.add(note);
					}
				}
				return ObjectMapper.notesToNoteResponses(listAllNoteOfCvAndRoundNum);
			} else
				throw new CustomNotFoundException(
						CustomError.builder().code("404").message("Not Found Any Note!").build());
		} else
			throw new CustomUnauthorizedException(CustomError.builder().code("401")
					.message("Access denied, you need to be Hr Manager to do this!").build());
	}

}