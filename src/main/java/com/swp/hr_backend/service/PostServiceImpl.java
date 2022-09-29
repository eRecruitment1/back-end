package com.swp.hr_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.swp.hr_backend.dto.PostDTO;
import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.repository.EmployeeRepository;
import com.swp.hr_backend.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final EmployeeRepository employeeRepository;

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
			return ObjectMapper
					.postToPostDTO(postRepository.save(ObjectMapper.postDTOToPost(post, employeeRepository)));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	@Override
	public PostDTO updatePost(PostDTO post) {
		try {
			Post postInDb = postRepository.findById(post.getPostId()).get();
			if (postInDb != null) {
				postInDb = postRepository.save(ObjectMapper.postDTOToPost(post, employeeRepository));
				if (postInDb != null)
					return post;
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

}
