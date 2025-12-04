package com.example.demo.Posts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Posts.PostService.postDTO;
import com.example.demo.config.RateLimit;

@RestController
@RequestMapping(path="api/posts")
public class PostController {
    
    private final PostService postService;
    
    @Autowired
    public PostController(PostService postService){
        this.postService = postService;
    }

    @PostMapping("/create")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> createPost(@RequestBody postDTO dto){
        return postService.createPost(dto.picture(), dto.user(), dto.caption());
    }

    @DeleteMapping("/delete/{id}")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id){
        return postService.deletePostById(id);
    }
    
    @PostMapping("/updateCaption/{id}")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> updatePost(@PathVariable("id") Long id, @RequestBody String caption){
        return postService.updateCaption(id, caption);
    }

    @GetMapping("/userPosts")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> getPostsByUserId(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = Long.valueOf(userDetails.getUsername());
        return postService.getPostsByUserId(userId);
    }

    @GetMapping("/getFeed")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> getFeed(){
        return postService.getFeed();
    }

    @PostMapping("/updateLikes/{id}")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> updateLikes(@PathVariable("id") Long id, @RequestBody int likeOrDislike){
        return postService.updateLikes(id, likeOrDislike);
    }

}
