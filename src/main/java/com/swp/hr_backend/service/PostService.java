package com.swp.hr_backend.service;

import java.util.List;


import com.swp.hr_backend.model.response.PostResponse;

public interface PostService {
    public List<PostResponse> getLastestPost();
    public List<PostResponse> findPostByTitle(String keyword);
}
