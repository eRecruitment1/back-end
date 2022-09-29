package com.swp.hr_backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hr_backend.dto.PostDTO;
import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;

import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.repository.PostRepository;
import com.swp.hr_backend.service.AccountService;
import com.swp.hr_backend.service.PostService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/post")
@RestController
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@GetMapping(value = "/getlastest")
	public ResponseEntity<List<PostDTO>> getLastest() throws CustomNotFoundException {
		List<PostDTO> postResponses = new ArrayList<>();
		postResponses = postService.getLastestPost();
		if (postResponses.isEmpty()) {
			throw new CustomNotFoundException(CustomError.builder().code("404").message("Not Found Anything").build());
		}
		return ResponseEntity.ok(postResponses);
	}

	@GetMapping(value = "/filter/get")
	public ResponseEntity<List<PostDTO>> filterByTitle(@RequestParam(name = "keyword") String keyword)
			throws CustomBadRequestException, CustomNotFoundException {
		List<PostDTO> postResponses = new ArrayList<>();
		if (keyword.trim().isEmpty()) {
			throw new CustomBadRequestException(CustomError.builder().code("403").message("keyword is null").build());
		}
		postResponses = postService.findPostByTitle(keyword);
		if (postResponses.isEmpty()) {
			throw new CustomNotFoundException(CustomError.builder().code("404").message("Not Found Anything").build());
		}
		return ResponseEntity.ok(postResponses);
	}

	@PostMapping("/create")
	public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO post) throws BaseCustomException {
		PostDTO postCreated = postService.createNewPost(post);
		if (postCreated == null) {
			CustomError error = new CustomError();
			error.setCode(HttpStatus.BAD_GATEWAY.toString());
			error.setMessage("Create Post Invalid Field!");
			error.setTable("Post");
			throw new BaseCustomException(error);
		} else {
			return new ResponseEntity<>(postCreated, HttpStatus.CREATED);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<PostDTO> updatePost(@RequestBody PostDTO post) throws BaseCustomException {
		PostDTO postUpdated = postService.updatePost(post);
		if (postUpdated == null) {
			CustomError error = new CustomError();
			error.setCode(HttpStatus.NOT_FOUND.toString());
			error.setMessage("Update Post Failed!");
			error.setTable("Post");
			throw new BaseCustomException(error);
		} else {
			return new ResponseEntity<>(postUpdated, HttpStatus.ACCEPTED);
		}
	}
}
