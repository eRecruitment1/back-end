package com.swp.hr_backend.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.exception.custom.BaseCustomException;
import com.swp.hr_backend.model.dto.PostDTO;

public interface PostService {
    public List<PostDTO> getLastestPost();
    public List<PostDTO> findPostByTitle(String keyword);
    
    public PostDTO createNewPost(PostDTO post) throws BaseCustomException;
    
    public PostDTO updatePost(PostDTO post) throws BaseCustomException;
    public Page<PostDTO> getAllPost(int pageNumber,int pageSize);
    public PostDTO getPostByID(int id);
    public List<PostDTO> getAllPost();
}
