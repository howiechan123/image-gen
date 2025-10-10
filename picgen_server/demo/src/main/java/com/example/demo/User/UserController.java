package com.example.demo.User;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.RateLimit;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpServlet;

@RestController
@RequestMapping(path= "api/Users")
public class UserController {
    
    @Autowired
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    @RateLimit(limit = 50, period = 60)
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/email")
    @RateLimit(limit = 50, period = 60)
    public Optional<User> getUserByEmail(@RequestParam("email") String email){
        return userService.getUserByEmail(email);
    }

    @PostMapping
    @RateLimit(limit = 50, period = 60)
    public void postUser(@RequestBody User user){
        userService.addUser(user);
    }



}
