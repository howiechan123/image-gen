package com.example.demo.Register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.RateLimit;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/public/register")
public class RegisterController {
    
    @Autowired
    private final RegisterService registerService;
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping
    @RateLimit(limit = 50, period = 60)
    public RegisterResponse registerUser(@RequestBody RegisterRequest registerRequest) {
        return registerService.registerUser(registerRequest);
    }

}
