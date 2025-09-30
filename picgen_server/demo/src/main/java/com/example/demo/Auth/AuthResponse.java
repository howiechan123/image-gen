package com.example.demo.Auth;
import com.example.demo.User.User;


public class AuthResponse {
    private boolean success;
    private String message;
    private User user;
    private String token;

    public AuthResponse(String message, boolean success, User user, String token) {
        this.message = message;
        this.success = success;
        this.user = user;
        this.token = token;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    
    

    
}
