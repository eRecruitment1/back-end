package com.swp.hr_backend.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.dto.PostDTO;
import com.swp.hr_backend.service.PostService;
import com.swp.hr_backend.utils.PageConstant;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/post")
@RestController
@RequiredArgsConstructor
@CrossOrigin
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
	public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostDTO post) throws BaseCustomException {
		PostDTO postCreated = postService.createNewPost(post);
		if (postCreated == null) {
			throw new CustomBadRequestException(CustomError.builder().code("403").message("post is null").build());
		} else {
			return new ResponseEntity<>(postCreated, HttpStatus.CREATED);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<PostDTO> updatePost(@Valid @RequestBody PostDTO post) throws BaseCustomException {
		PostDTO postUpdated = postService.updatePost(post);
		if (postUpdated == null) {
			throw new CustomNotFoundException(CustomError.builder().code("404").message("Not Found Anything").build());
		} else {
			return new ResponseEntity<>(postUpdated, HttpStatus.ACCEPTED);
		}
	}

	@GetMapping(value = "/getAll")
	public ResponseEntity<Page<PostDTO>> getMethodName(
			@RequestParam(name = "page", required = false, defaultValue = PageConstant.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(name = "size", required = false, defaultValue = PageConstant.DEFAULT_PAGE_SIZE) int size) throws CustomNotFoundException {
		Page<PostDTO> postDTO = postService.getAllPost(page, size);
		if (!postDTO.hasContent()) {
			throw new CustomNotFoundException(CustomError.builder().code("404").message("not found anything").build());
		}
		return ResponseEntity.ok(postDTO);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<PostDTO> getPostByID(@PathVariable("id") int id) throws CustomNotFoundException {
		PostDTO postDTO = postService.getPostByID(id);
		if (postDTO == null) {
			throw new CustomNotFoundException(CustomError.builder().code("404").message("Not found any post").build());
		}
		return ResponseEntity.ok(postDTO);
	}
	@GetMapping("/getAllPost")
	public List<PostDTO> getAllPost() {
		return postService.getAllPost();
	}
	
}
