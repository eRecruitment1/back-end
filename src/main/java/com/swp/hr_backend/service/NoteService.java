package com.swp.hr_backend.service;

import java.util.List;

import com.swp.hr_backend.entity.Note;
import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.model.request.EvaluateRequest;
import com.swp.hr_backend.model.request.NoteRequest;
import com.swp.hr_backend.model.request.ViewNoteRequest;
import com.swp.hr_backend.model.response.NoteResponse;

public interface NoteService {
	public NoteResponse takeNote(NoteRequest note) throws BaseCustomException;
	public List<NoteResponse> viewNotes() throws BaseCustomException;
	public List<NoteResponse> viewNotesByCvIdAndRoundNum(ViewNoteRequest viewNoteReq) throws BaseCustomException;
}
