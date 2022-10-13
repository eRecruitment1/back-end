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
import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.entity.ScheduleDetail;
import com.swp.hr_backend.entity.UserCV;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.model.request.UserCVUploadRequest;
import com.swp.hr_backend.model.response.UserCVUploadResponse;
import com.swp.hr_backend.repository.CandidateRepository;
import com.swp.hr_backend.repository.FinalResultRepository;
import com.swp.hr_backend.repository.PostRepository;
import com.swp.hr_backend.repository.UserCVRepository;
import com.swp.hr_backend.utils.AccountRole;
import com.swp.hr_backend.utils.JwtTokenUtil;
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
        throw new CustomDuplicateFieldException(
            CustomError.builder().code("duplicate cv").field("userCV").message("You Have uploaded one CV").build());
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
            .filter(scheduleDetail -> scheduleDetail.getInterviewer().getAccountID().equalsIgnoreCase(account.getAccountID()))
            .filter(scheduleDetail-> scheduleDetail.isStatus())
            .collect(Collectors.toList());
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
}
