package com.swp.hr_backend.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.swp.hr_backend.entity.Post;
@Repository
public interface PostRepository extends PagingAndSortingRepository<Post,Integer>{
    @Query(value = "SELECT TOP 6 * FROM dbo.post WHERE status = 1 ORDER BY post_id DESC" , nativeQuery = true)
    public List<Post> getLastestPost();
    @Query("SELECT p FROM Post p WHERE p.title LIKE %?1% AND p.status = true")
    List<Post> findPostByTitle(String search, Sort sort);
}
