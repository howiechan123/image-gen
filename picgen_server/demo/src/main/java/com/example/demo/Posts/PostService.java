package com.example.demo.Posts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    @Autowired
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    public ResponseEntity<?> createPost(){

    }

    public ResponseEntity<?> deletePost(){

    }

    public ResponseEntity<?> updatePost(){

    }

    public ResponseEntity<?> getPostsByUser(){

    }

    public ResponseEntity<?> getFeed(){
        
    }


    public record postResponse(postDTO dto, boolean success, String message){}

    public record postDTO() {}
}
