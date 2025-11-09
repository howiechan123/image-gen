package com.example.demo.Comments;

import com.example.demo.Posts.Post;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_sequence")
    @SequenceGenerator(name = "comment_sequence", sequenceName = "comment_seq", allocationSize = 1)
    private Long commentId;
    private String comment;
    private Long likes;

    @OneToOne
    @JoinColumn(name="post_id", nullable=false)
    private Post post;
}
