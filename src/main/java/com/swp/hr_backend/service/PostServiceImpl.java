package com.swp.hr_backend.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.dto.PostDTO;
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.repository.AccountRepository;
import com.swp.hr_backend.repository.EmployeeRepository;
import com.swp.hr_backend.repository.PostRepository;
import com.swp.hr_backend.utils.AccountRole;
import com.swp.hr_backend.utils.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final EmployeeRepository employeeRepository;
	private final JwtTokenUtil jwtTokenUtil;
	private final AccountRepository accountRepository;

	@Override
	public List<PostDTO> getLastestPost() {
		List<Post> lPosts = new ArrayList<>();
		List<PostDTO> postResponses = new ArrayList<>();
		lPosts = postRepository.getLastestPost();
		for (Post post : lPosts) {
			postResponses.add(ObjectMapper.postToPostDTO(post));
		}
		return postResponses;
	}

	@Override
	public List<PostDTO> findPostByTitle(String keyword) {
		List<Post> posts = new ArrayList<>();
		List<PostDTO> postResponses = new ArrayList<>();
		posts = postRepository.findPostByTitle(keyword, Sort.by(Sort.Direction.DESC, "id"));
		if (posts != null) {
			for (Post post : posts) {
				postResponses.add(ObjectMapper.postToPostDTO(post));
			}
			return postResponses;
		}
		return null;
	}

	@Override
	public PostDTO createNewPost(PostDTO post) throws BaseCustomException {
		if (post != null) {
			Account acc = jwtTokenUtil.loggedAccount();
			if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HREMPLOYEE)) {
				post.setStatus(true);
				post.setStartTime(new Timestamp(System.currentTimeMillis()));
				post.setAccountId(acc.getAccountID());
				return ObjectMapper
						.postToPostDTO(postRepository.save(ObjectMapper.postDTOToPost(post, employeeRepository)));
			} else throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
						.message("Access denied, you need to be Hr Employee to do this!").build());
		}
		return null;
	}

	@Override
	public PostDTO updatePost(PostDTO post) throws BaseCustomException {
		if (post != null) {
			Account acc = jwtTokenUtil.loggedAccount();
			if (jwtTokenUtil.checkPermissionAccount(acc, AccountRole.HREMPLOYEE)) {
				post.setStartTime(new Timestamp(System.currentTimeMillis()));
				post.setAccountId(acc.getAccountID());
				Post postInDb = postRepository.findById(post.getPostId()).get();
				if (postInDb != null) {
					postInDb = postRepository.save(ObjectMapper.postDTOToPost(post, employeeRepository));
					if (postInDb != null)
						return post;
				} else
					throw new CustomNotFoundException(
							CustomError.builder().code("404").message("not found response").build());
			} else
				throw new CustomUnauthorizedException(CustomError.builder().code("unauthorized")
						.message("Access denied, you need to be Hr Employee to do this!").build());
		}
		return null;
	}
	@Override
	public Page<PostDTO> getAllPost(int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		List<Post> postList = postRepository.SelectAll();
		List<PostDTO> postDTOList = new ArrayList<>();
		for (Post post : postList) {
			if (post.isStatus()) {
				PostDTO postDTO = ObjectMapper.postToPostDTO(post);
				postDTOList.add(postDTO);
			}
		}
		Pageable paging = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
		int start = Math.min((int) paging.getOffset(), postDTOList.size());
		int end = Math.min((start + paging.getPageSize()), postDTOList.size());
		Page<PostDTO> pagePostDTO = new PageImpl<>(postDTOList.subList(start, end), pageable, postDTOList.size());
		return pagePostDTO;
	}
	@Override
	public PostDTO getPostByID(int id) {
		Optional<Post> post = postRepository.findById(id);
		if(post.isPresent()){
			return ObjectMapper.postToPostDTO(post.get());
		}
		return null;
	}

	@Override
	public List<PostDTO> getAllPost() {
		List<PostDTO> postDTOs = new ArrayList<>();
		Iterable<Post> postList =  postRepository.findAll();
        for (Post post : postList) {
		    String id = post.getEmployee().getAccountID();
			Account account = accountRepository.findById(id).get();
			PostDTO postDTO = ObjectMapper.postToPostDTO(post);
			postDTO.setFullName(account.getFirstname() + " " + account.getLastname());
			postDTO.setUsername(account.getUsername());
			postDTOs.add(postDTO);
		}
		return postDTOs;
	}

}
