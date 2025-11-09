package com.example.demo.Posts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {
    
    private final PostService postService;
    
    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(){
        
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePost(){

    }

    
}
