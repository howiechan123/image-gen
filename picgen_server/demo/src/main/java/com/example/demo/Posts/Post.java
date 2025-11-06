package com.example.demo.Posts;

import java.time.LocalDateTime;

import com.example.demo.Pictures.Picture;
import com.example.demo.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


@Entity
@Table
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_sequence")
    @SequenceGenerator(name = "post_sequence", sequenceName = "post_seq", allocationSize = 1)
    private Long postId;
    private int likeCount;
    private LocalDateTime timePosted;

    @ManyToOne
    @JoinColumn(name="user_Id", nullable=false)
    @JsonBackReference
    private User user;

    @OneToOne
    @JoinColumn(name="picture_id", nullable=false)
    private Picture picture;
    

    public Post(Picture picture, User user, int likeCount, LocalDateTime timePosted){
        this.picture = picture;
        this.user = user;
        this.likeCount = likeCount;
        this.timePosted = timePosted;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Picture getPicture() {
        return picture;
    }
    public void setPicture(Picture picture) {
        this.picture = picture;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    public LocalDateTime getTimePosted() {
        return timePosted;
    }
    public void setTimePosted(LocalDateTime timePosted) {
        this.timePosted = timePosted;
    }

    
}
