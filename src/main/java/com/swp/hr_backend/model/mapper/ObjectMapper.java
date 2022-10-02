package com.swp.hr_backend.model.mapper;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.entity.Employee;
import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.model.dto.PostDTO;
import com.swp.hr_backend.model.response.LoginResponse;
import com.swp.hr_backend.model.response.ProfileResponse;
import com.swp.hr_backend.repository.EmployeeRepository;

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

}
