package com.swp.hr_backend.service;

import java.util.List;

import com.swp.hr_backend.dto.PostDTO;
import com.swp.hr_backend.entity.Post;

public interface PostService {
    public List<PostDTO> getLastestPost();
    public List<PostDTO> findPostByTitle(String keyword);
    
    public PostDTO createNewPost(PostDTO post);
    
    public PostDTO updatePost(PostDTO post);
}
