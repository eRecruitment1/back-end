package com.swp.hr_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.swp.hr_backend.entity.Post;
@Repository
public interface PostRepository extends PagingAndSortingRepository<Post,Integer>{
    @Query(value = "Select top 6 * from dbo.post order by post_id desc" , nativeQuery = true)
    public List<Post> getLastestPost();
}
