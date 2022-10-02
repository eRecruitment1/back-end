package com.swp.hr_backend.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.swp.hr_backend.entity.Account;
import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.exception.custom.CustomUnauthorizedException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.dto.PostDTO;
import com.swp.hr_backend.model.mapper.ObjectMapper;
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
	public PostDTO createNewPost(PostDTO post) {
		try {
			if (post != null) {
				Account acc = jwtTokenUtil.loggedAccount();
				if (jwtTokenUtil.checkPermissionCurrentAccount(acc, AccountRole.HREMPLOYEE)) {
					post.setStatus(true);
					post.setStartTime(new Timestamp(System.currentTimeMillis()));
					post.setAccountId(acc.getAccountID());
					return ObjectMapper
							.postToPostDTO(postRepository.save(ObjectMapper.postDTOToPost(post, employeeRepository)));
				} else throw new CustomUnauthorizedException(
						CustomError.builder().code("unauthorized").message("Access denied, you need to be Hr Employee to do this!").build());
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	@Override
	public PostDTO updatePost(PostDTO post) {
		try {
			if (post != null) {
				Account acc = jwtTokenUtil.loggedAccount();
				if (jwtTokenUtil.checkPermissionCurrentAccount(acc, AccountRole.HREMPLOYEE)) {
					post.setStartTime(new Timestamp(System.currentTimeMillis()));
					post.setAccountId(acc.getAccountID());
					Post postInDb = postRepository.findById(post.getPostId()).get();
					if (postInDb != null) {
						postInDb = postRepository.save(ObjectMapper.postDTOToPost(post, employeeRepository));
						if (postInDb != null)
							return post;
					} else throw new CustomNotFoundException(CustomError.builder().code("404").message("not found response").build());
				} else throw new CustomUnauthorizedException(
						CustomError.builder().code("unauthorized").message("Access denied, you need to be Hr Employee to do this!").build());
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

}
