package com.example.demo.Posts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query("SELECT p FROM Post WHERE p.user.id = ?1")
    public ArrayList<Post> findPostsByUserId(Long userId);

    @Query("SELECT p FROM Post p")
    public ArrayList<Post> findAllPosts();
}
