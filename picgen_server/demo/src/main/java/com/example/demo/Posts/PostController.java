package com.example.demo.Posts;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Comments.Comment;
import com.example.demo.Pictures.Picture;
import com.example.demo.Posts.PostService.postDTO;
import com.example.demo.User.User;

@RestController
@RequestMapping("/posts")
public class PostController {
    
    private final PostService postService;
    
    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody postDTO dto){
        return postService.createPost(dto.picture(), dto.user(), dto.caption());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id){
        return postService.deletePostById(id);
    }
    
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePost(@PathVariable("id") Long id, @RequestBody postDTO dto){
        return postService.updatePostById(id, dto);
    }

    @GetMapping("/userPosts/{id}")
    public ResponseEntity<?> getPostsByUserId(@PathVariable("id") Long userId){
        return postService.getPostsByUserId(userId);
    }

    @GetMapping("/getFeed")
    public ResponseEntity<?> getFeed(){
        return postService.getFeed();
    }

}
