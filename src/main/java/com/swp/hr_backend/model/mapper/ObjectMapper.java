package com.swp.hr_backend.model.mapper;

import com.swp.hr_backend.entity.*;
import com.swp.hr_backend.model.dto.PostDTO;
import com.swp.hr_backend.model.request.NoteRequest;
import com.swp.hr_backend.model.response.LoginResponse;
import com.swp.hr_backend.model.response.NoteResponse;
import com.swp.hr_backend.model.response.ProfileResponse;
import com.swp.hr_backend.model.response.ScheduleDetailResponse;
import com.swp.hr_backend.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;

public class ObjectMapper {
	public static LoginResponse accountToLoginResponse(Account account) {
		LoginResponse loginResponse = LoginResponse.builder().id(account.getAccountID()).username(account.getUsername())
				.firstName(account.getFirstname()).lastName(account.getLastname()).email(account.getEmail())
				.gender(account.isGender()).status(account.isStatus()).phone(account.getPhone())
				.urlImg(account.getUrlImg()).build();
		return loginResponse;

	}

	public static ProfileResponse accountToProfileResponse(Account account) {
		ProfileResponse profileResponse = ProfileResponse.builder().id(account.getAccountID())
				.username(account.getUsername()).firstName(account.getFirstname()).lastName(account.getLastname())
				.email(account.getEmail()).gender(account.isGender()).phone(account.getPhone())
				.urlImg(account.getUrlImg()).build();
		return profileResponse;

	}

	public static PostDTO postToPostDTO(Post post) {
		PostDTO postDTO = new PostDTO();
		postDTO.setPostId(post.getPostID());
		postDTO.setTitle(post.getTitle());
		postDTO.setDescription(post.getDescription());
		postDTO.setStartTime(post.getStartTime());
		postDTO.setStatus(post.isStatus());
		postDTO.setThumbnailUrl(post.getThumbnailUrl());
		postDTO.setAccountId(post.getEmployee().getAccountID());
		return postDTO;
	}

	public static Post postDTOToPost(PostDTO postDTO, EmployeeRepository repo) {
		Post post = new Post();
		post.setPostID(postDTO.getPostId());
		post.setTitle(postDTO.getTitle());
		post.setDescription(postDTO.getDescription());
		post.setStartTime(postDTO.getStartTime());
		post.setStatus(postDTO.isStatus());
		post.setThumbnailUrl(postDTO.getThumbnailUrl());
		if(repo != null) {
			Employee employee = repo.findById(postDTO.getAccountId()).get();
			if(employee != null) post.setEmployee(employee);
		}
		return post;
	}

	public static ScheduleDetailResponse scheduleToScheduleDetailResponse(Schedule schedule, ScheduleDetail scheduleDetail, List<String> interviewerIDs){
		ScheduleDetailResponse scheduleDetailResponse = ScheduleDetailResponse.builder()
				.scheduleID(schedule.getScheduleID())
				.date(schedule.getDate())
				.status(scheduleDetail.isStatus())
				.urlMeeting(scheduleDetail.getUrlMeeting())
				.startTime(scheduleDetail.getStartTime())
				.endTime(scheduleDetail.getEndTime())
				.roundNum(scheduleDetail.getRoundNum())
				.cvID(scheduleDetail.getUserCV().getCvID())
				.interviewerID(interviewerIDs)
				.build();
		return scheduleDetailResponse;
	}
	
	public static NoteResponse noteToNoteResponse(Note note) {
		return NoteResponse.builder().id(note.getNoteID())
									.message(note.getMessage())
									.point(note.getPoint())
									.scheduleId(note.getScheduleDetail().getScheduleDetailID().getScheduleID())
									.accountId(note.getScheduleDetail().getScheduleDetailID().getInterviewerID())
									.cvId(note.getScheduleDetail().getScheduleDetailID().getCvID())
									.build();
	}
	
	public static Note noteRequestToNote(NoteRequest note, ScheduleDetail scheduleDetail) {
		return Note.builder().message(note.getMessage())
							 .point(note.getPoint())
							 .scheduleDetail(scheduleDetail)
							 .build();
	}
	
	public static List<NoteResponse> notesToNoteResponses(List<Note> notes) {
		List<NoteResponse> listRes = new ArrayList<NoteResponse>();
		if(notes != null && notes.size() > 0) {
			for(Note note : notes) {
				listRes.add(noteToNoteResponse(note));
			}
		} else return null;
		return listRes;
	}
}
