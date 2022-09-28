package com.swp.hr_backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp.hr_backend.exception.custom.CustomBadRequestException;
import com.swp.hr_backend.exception.custom.CustomNotFoundException;
import com.swp.hr_backend.model.CustomError;
import com.swp.hr_backend.model.response.PostResponse;
import com.swp.hr_backend.service.PostService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;




@RequestMapping("/api/post")
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    @GetMapping(value="/getlastest")
    public ResponseEntity<List<PostResponse>> getLastest() throws CustomNotFoundException {
        List<PostResponse> postResponses = new ArrayList<>();
        postResponses = postService.getLastestPost();
        if(postResponses.isEmpty()){
            throw new CustomNotFoundException(CustomError.builder().code("404").message("Not Found Anything").build());
        }
        return ResponseEntity.ok(postResponses);
    }
    @GetMapping(value="/filter/get")
    public ResponseEntity<List<PostResponse>> filterByTitle(@RequestParam(name = "keyword") String keyword) throws CustomBadRequestException, CustomNotFoundException {
        List<PostResponse> postResponses = new ArrayList<>();
        if(keyword.trim().isEmpty()){
            throw new CustomBadRequestException(CustomError.builder().code("403").message("keyword is null").build());
        }
        postResponses = postService.findPostByTitle(keyword);
         if(postResponses.isEmpty()){
            throw new CustomNotFoundException(CustomError.builder().code("404").message("Not Found Anything").build());
        }
        return ResponseEntity.ok(postResponses);
    }
    
}
