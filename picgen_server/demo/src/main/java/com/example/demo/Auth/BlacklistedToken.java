package com.example.demo.Auth;

import jakarta.persistence.*;
import java.util.Date;

import jakarta.persistence.Entity;

import jakarta.persistence.Table;

import jakarta.persistence.Id;

import jakarta.persistence.GeneratedValue;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Column;

import java.util.Date;

@Entity
@Table
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private Date expiry;

    public BlacklistedToken() {}

    public BlacklistedToken(String token, Date expiry) {
        this.token = token;
        this.expiry = expiry;
    }

    // Getters & setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Date getExpiry() { return expiry; }
    public void setExpiry(Date expiry) { this.expiry = expiry; }
}
