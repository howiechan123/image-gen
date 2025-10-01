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
    private String deleteUrl;

    @ManyToOne
    @JoinColumn(name="userId", nullable=false)
    @JsonBackReference
    private User user;

    public Picture(Long pictureId, String filePath, String fileName, User user, String deleteUrl) {
        this.pictureId = pictureId;
        this.filePath = filePath;
        this.fileName = fileName;
        this.user = user;
        this.deleteUrl = deleteUrl;
    }

    public Picture() {
    }

    public Picture(Long pictureId, String filePath) {
        this.pictureId = pictureId;
        this.filePath = filePath;
    }

    public Picture(Long pictureId, String filePath, String deleteUrl) {
        this.pictureId = pictureId;
        this.filePath = filePath;
        this.deleteUrl = deleteUrl;
    }

    public Picture(String fileName, String filePath, User user) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.user = user;
    }

    public Picture(String fileName, String filePath, User user, String deleteUrl) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.user = user;
        this.deleteUrl = deleteUrl;
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

    public String getDeleteUrl() {
        return deleteUrl;
    }

    public void setDeleteUrl(String deleteUrl) {
        this.deleteUrl = deleteUrl;
    }

    

    

}
