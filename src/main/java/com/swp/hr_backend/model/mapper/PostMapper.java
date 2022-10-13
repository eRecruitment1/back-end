package com.swp.hr_backend.model.mapper;

import java.util.List;

import com.swp.hr_backend.entity.Post;
import com.swp.hr_backend.model.dto.PostDTO;



public interface PostMapper {
	PostDTO postToPostDTO(Post post);
	List<PostDTO> postsToPostDTOs(List<Post> posts);
	Post postDTOToPost(PostDTO post);
}
