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
import com.swp.hr_backend.exception.custom.CustomDuplicateFieldException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.request.EvaluateRequest;
import com.swp.hr_backend.model.request.UserCVUploadRequest;
import com.swp.hr_backend.model.response.UserCVUploadResponse;
import com.swp.hr_backend.service.CVService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/userCV")
@RestController
@RequiredArgsConstructor
@CrossOrigin
public class UserCVController {
    private final CVService cvService;
    @PostMapping("/upload")
    public ResponseEntity<UserCVUploadResponse> uploadCV(@RequestBody UserCVUploadRequest userCVRequest) throws CustomBadRequestException, CustomDuplicateFieldException, CustomUnauthorizedException {
         UserCVUploadResponse userCVUploadResponse = cvService.uploadCV(userCVRequest);
         if(userCVUploadResponse == null){
            throw new CustomBadRequestException(CustomError.builder().code("403").message("Bad Request").build());
         }
        return ResponseEntity.ok(userCVUploadResponse);
    }
    @GetMapping(value="/view")
    public ResponseEntity<List<UserCVUploadResponse>> viewCV() throws CustomNotFoundException {
        List<UserCVUploadResponse> userCVUploadResponses = cvService.viewCV();
        // if(userCVUploadResponses == null){
        //     throw new CustomNotFoundException(CustomError.builder().code("404").message("Not found any CV appropriate").build());
        // }
        return ResponseEntity.ok(userCVUploadResponses);
    }
    
	@PostMapping("/evaluate")
	public ResponseEntity<Boolean> takeNote(@Valid @RequestBody EvaluateRequest evaReq) throws BaseCustomException {
		boolean result = cvService.evaluateUserCV(evaReq);
		if (!result) {
			throw new CustomBadRequestException(CustomError.builder().code("403").message("evaluate failed...").build());
		} else {
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
	}
}

