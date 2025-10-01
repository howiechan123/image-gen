package com.example.demo.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/public/auth")
public class AuthController {

    @Autowired
    private final AuthService loginService;
    public AuthController(AuthService loginService) {
        this.loginService = loginService;
    }

    

    @PostMapping
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest loginRequest) {
        
        return loginService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
    
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        System.out.println("OOOOOOOOOOOOOOOOO");
        return loginService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        return loginService.logout(response, refreshToken);
    }
    


}
