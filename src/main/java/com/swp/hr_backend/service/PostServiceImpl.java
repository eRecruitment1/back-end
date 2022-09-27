package com.swp.hr_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.model.mapper.ObjectMapper;
import com.swp.hr_backend.model.response.PostResponse;
import com.swp.hr_backend.repository.PostRepository;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    @Override
    public List<PostResponse> getLastestPost() {
       List<Post> lPosts = new ArrayList<>();
       List<PostResponse> postResponses = new ArrayList<>();
       lPosts = postRepository.getLastestPost();
       for (Post post : lPosts) {
           postResponses.add(ObjectMapper.postToPostResponse(post));
       }
        return postResponses;
    }
    
}
