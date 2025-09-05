package com.example.demo.Pictures;

import com.example.demo.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table
public class Picture {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "picture_sequence")
    @SequenceGenerator(name = "picture_sequence", sequenceName = "picture_seq", allocationSize = 1)
    private Long pictureId;
    private String filePath;
    private String fileName;

    @ManyToOne
    @JoinColumn(name="userId", nullable=false)
    @JsonBackReference
    private User user;

    public Picture(Long pictureId, String filePath, String fileName, User user) {
        this.pictureId = pictureId;
        this.filePath = filePath;
        this.fileName = fileName;
        this.user = user;
    }

    public Picture() {
    }

    public Picture(Long pictureId, String filePath) {
        this.pictureId = pictureId;
        this.filePath = filePath;
    }

    public Picture(String fileName, String filePath, User user) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.user = user;
    }

    public Long getPictureId() {
        return this.pictureId;
    }

    public void setPictureId(Long pictureId) {
        this.pictureId = pictureId;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    

    

}
