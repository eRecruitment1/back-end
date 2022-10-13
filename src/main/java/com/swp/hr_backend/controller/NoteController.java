package com.swp.hr_backend.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.dto.PostDTO;
import com.swp.hr_backend.model.request.NoteRequest;
import com.swp.hr_backend.model.request.ViewNoteRequest;
import com.swp.hr_backend.model.response.NoteResponse;
import com.swp.hr_backend.service.NoteService;
import com.swp.hr_backend.service.PostService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/note")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class NoteController {
	
	private final NoteService noteService;
	
	@PostMapping("/takeNote")
	public ResponseEntity<NoteResponse> takeNote(@Valid @RequestBody NoteRequest noteReq) throws BaseCustomException {
		NoteResponse noteRes = noteService.takeNote(noteReq);
		if (noteRes == null) {
			throw new CustomBadRequestException(CustomError.builder().code("403").message("note is null").build());
		} else {
			return new ResponseEntity<>(noteRes, HttpStatus.CREATED);
		}
	}
	
	@GetMapping(value = "/getAll")
	public ResponseEntity<List<NoteResponse>> getAllNote() throws BaseCustomException {
		List<NoteResponse> list = noteService.viewNotes();
		if (list == null) {
			throw new CustomBadRequestException(CustomError.builder().code("403").message("list note is null").build());
		} else {
			return new ResponseEntity<>(list, HttpStatus.OK);
		}
	}
	
	@PostMapping("/get/cvid")
	public ResponseEntity<List<NoteResponse>> getNotesByViewNoteRequest(@Valid @RequestBody ViewNoteRequest viewNoteReq) throws BaseCustomException {
		List<NoteResponse> list = noteService.viewNotesByCvIdAndRoundNum(viewNoteReq);
		if (list == null) {
			throw new CustomBadRequestException(CustomError.builder().code("403").message("list note is null").build());
		} else {
			return new ResponseEntity<>(list, HttpStatus.OK);
		}
	}
}
