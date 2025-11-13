package com.example.demo.Posts;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Comments.Comment;
import com.example.demo.Pictures.Picture;
import com.example.demo.User.User;

@Service
public class PostService {
    @Autowired
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    public ResponseEntity<?> createPost(Picture picture, User user, String caption){
        ArrayList<Comment> comments = new ArrayList<Comment>();
        LocalDateTime time = LocalDateTime.now();
        Post post = new Post(picture, user, 0L, time, caption, comments);
        post = postRepository.save(post);
        postDTO dto = new postDTO(picture, user, 0L, time, caption, comments);
        postResponse response = new postResponse(dto, true, "post created");

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> deletePostById(Long id){
        postRepository.deleteById(id);
        return ResponseEntity.ok(new postResponse(null, true, "post id:" + id + " deleted" ));
    }

    public ResponseEntity<?> updatePostById(Long id, postDTO dto){
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalStateException("post does not exist"));
        post.setPicture(dto.picture());
        post.setUser(dto.user());
        post.setLikeCount(dto.likeCount());
        post.setTimePosted(dto.timePosted());
        post.setCaption(dto.caption());
        post.setComments(dto.comments());
        return ResponseEntity.ok(new postResponse(null, true, "post updated"));
    }

    public ResponseEntity<?> getPostsByUserId(Long id){
        ArrayList<Post> posts = postRepository.findPostsByUserId(id);
        postListDTO dto = new postListDTO(posts);
        return ResponseEntity.ok(new postListResponse(dto, true, "get posts by user"));
    }

    public ResponseEntity<?> getFeed(){
        ArrayList<Post> posts = postRepository.findAllPosts();
        postListDTO dto = new postListDTO(posts);
        return ResponseEntity.ok(new postListResponse(dto, true, "get all posts"));
    }


    public record postResponse(postDTO dto, boolean success, String message){}

    public record postListResponse(postListDTO posts, boolean success, String message){}

    public record postDTO(Picture picture, User user, Long likeCount, LocalDateTime timePosted, String caption, ArrayList<Comment> comments) {}

    public record postListDTO(ArrayList<Post> posts){}
}
