package com.swp.hr_backend.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.entity.Candidate;
import com.swp.hr_backend.entity.FinalResult;
import com.swp.hr_backend.entity.Note;
import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.entity.ScheduleDetail;
import com.swp.hr_backend.entity.UserCV;
import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.model.request.EvaluateRequest;
import com.swp.hr_backend.model.request.UserCVUploadRequest;
import com.swp.hr_backend.model.response.UserCVUploadResponse;
import com.swp.hr_backend.repository.CandidateRepository;
import com.swp.hr_backend.repository.FinalResultRepository;
import com.swp.hr_backend.repository.NoteRepository;
import com.swp.hr_backend.repository.PostRepository;
import com.swp.hr_backend.repository.UserCVRepository;
import com.swp.hr_backend.utils.AccountRole;
import com.swp.hr_backend.utils.JwtTokenUtil;
import com.swp.hr_backend.utils.ResultStatus;
import com.swp.hr_backend.utils.Round;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CVServiceImpl implements CVService {
	private final JwtTokenUtil jwtTokenUtil;
	private final PostRepository postRepository;
	private final UserCVRepository userCVRepository;
	private final FinalResultRepository finalResultRepository;
	private final CandidateRepository candidateRepository;
	private final EmployeeService employeeService;
	private final RoleService roleService;
	private final UserCVRepository userCvRepo;
	private final NoteRepository noteRepo;

	@Override
	public UserCVUploadResponse uploadCV(UserCVUploadRequest cvRequest)
			throws CustomDuplicateFieldException, CustomBadRequestException, CustomUnauthorizedException {
		Account account = jwtTokenUtil.loggedAccount();
		UserCV userCV = new UserCV();
		if (candidateRepository.findById(account.getAccountID()).isEmpty()) {
			throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
					.message("Access denied, you need to be Candidate to do this!").build());
		}
		if (jwtTokenUtil.checkPermissionAccount(account, AccountRole.CANDIDATE)) {
			Optional<Post> postOptional = postRepository.findById(cvRequest.getPostID());
			Post post = null;
			if (postOptional.isEmpty()) {
				return null;
			} else {
				post = postOptional.get();
			}
			userCV.setPost(post);
			userCV.setApplyTime(new Timestamp(System.currentTimeMillis()));
			userCV.setCandidate((Candidate) account);
			if (cvRequest.getLinkCV().trim() != null) {
				userCV.setLinkCV(cvRequest.getLinkCV());
			} else {
				return null;
			}
			Candidate candidate = candidateRepository.findById(account.getAccountID()).get();
			if (candidate.getUserCV() != null) {
				throw new CustomDuplicateFieldException(CustomError.builder().code("duplicate cv").field("userCV")
						.message("You Have uploaded one CV").build());
			}
			UserCV userCVSave = userCVRepository.save(userCV);
			FinalResult finalResult = new FinalResult();
			finalResult.setUserCV(userCVSave);
			finalResult.setResultStatus(Round.PENDING.toString());
			FinalResult finalResultSave = finalResultRepository.save(finalResult);
			return ObjectMapper.userCVToUserCVResponse(userCVSave);
		}
		return null;
	}

	@Override
	public List<UserCVUploadResponse> viewCV() {
		Account account = jwtTokenUtil.loggedAccount();
		List<UserCVUploadResponse> userCVUploadResponses = new ArrayList<>();
		Optional<String> roleNameOptional = Optional.empty();
		String roleName = "";
		Integer roleID = employeeService.findRoleIDByAccountID(account.getAccountID());
		if (roleID != null) {
			roleNameOptional = roleService.findRolenameByRoleID(roleID);
			roleName = roleNameOptional.get();
		} else {
			roleName = AccountRole.CANDIDATE.toString();
		}
		if (roleName.equalsIgnoreCase("EMPLOYEE")) {
			List<UserCV> userCVs = userCVRepository.findAll();
			List<UserCVUploadResponse> cvResult = new ArrayList<>();
			for (UserCV u : userCVs) {
				List<ScheduleDetail> scheduleDetails = u.getScheduleDetails();
				List<ScheduleDetail> result = scheduleDetails.stream()
						.filter(scheduleDetail -> scheduleDetail.getInterviewer().getAccountID()
								.equalsIgnoreCase(account.getAccountID()))
						.filter(scheduleDetail -> scheduleDetail.isStatus()).collect(Collectors.toList());
				if (!result.isEmpty()) {
					cvResult.add(ObjectMapper.userCVToUserCVResponse(u));
				}
			}
			return cvResult;
		}
		if (roleName.equalsIgnoreCase("HRMANAGER") || roleName.equalsIgnoreCase("HREMPLOYEE")) {
			List<UserCV> userCVs = userCVRepository.findAll();
			for (UserCV u : userCVs) {
				userCVUploadResponses.add(ObjectMapper.userCVToUserCVResponse(u));
			}
			return userCVUploadResponses;
		}
		if (jwtTokenUtil.checkPermissionAccount(account, AccountRole.CANDIDATE)) {
			Candidate candidate = candidateRepository.findById(account.getAccountID()).get();
			UserCV userCV = userCVRepository.findByCandidate(candidate);
			if (userCV != null) {
				userCVUploadResponses.add(ObjectMapper.userCVToUserCVResponse(userCV));
				return userCVUploadResponses;
			}
			return null;
		}
		return null;
	}

	@Override
	public boolean evaluateUserCV(EvaluateRequest evaluate) throws BaseCustomException {
		if (evaluate == null)
			throw new CustomNotFoundException(CustomError.builder().code("400").message("Bad Request").build());
		Account curAcc = jwtTokenUtil.loggedAccount();
		if (curAcc == null)
			throw new CustomUnauthorizedException(
					CustomError.builder().code("401").message("Access denied, you need to login to do this!").build());
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
							throw new CustomNotFoundException(CustomError.builder().code("403")
									.message("Still on meeting, cannot evaluate cv...").build());
						if (evaluate.isPass()) {
							cv.getFinalResult().setResultStatus(ResultStatus.FIRSTROUND.toString());
						} else {
							cv.getFinalResult().setResultStatus(ResultStatus.NOTPASS.toString());
						}
					} else
						throw new CustomNotFoundException(CustomError.builder().code("403")
								.message("UserCV status isn't at state pending, its through this state!").build());
					if (finalResultRepository.save(cv.getFinalResult()) != null)
						return true;
				}
				if (jwtTokenUtil.checkPermissionAccount(curAcc, AccountRole.HRMANAGER)) {
					if (resultStatus.toLowerCase().equals(ResultStatus.FIRSTROUND.toString().toLowerCase())) {
						if (!checkNumNoteToEvaluate(cv, Round.ROUND2, 6))
							throw new CustomNotFoundException(CustomError.builder().code("403")
									.message("Not enough note(6 note) to evaluate, cannot evaluate cv...").build());
						if (!checkAvailableScheduleDetail(cv, Round.ROUND2))
							throw new CustomNotFoundException(CustomError.builder().code("403")
									.message("Still on meeting, cannot evaluate cv...").build());
						if (evaluate.isPass()) {
							cv.getFinalResult().setResultStatus(ResultStatus.PASS.toString());
						} else {
							cv.getFinalResult().setResultStatus(ResultStatus.NOTPASS.toString());
						}
					} else
						throw new CustomNotFoundException(CustomError.builder().code("403")
								.message("UserCV status isn't at state first round, its through this state!").build());
					if (finalResultRepository.save(cv.getFinalResult()) != null)
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
		if (list != null && list.size() > 0) {
			int count = 0;
			for (Note note : list) {
				if (note.getScheduleDetail().getRoundNum().equalsIgnoreCase(round.toString())) {
					count++;
				}
			}
			if (count == num)
				return true;
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
