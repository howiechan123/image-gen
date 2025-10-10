package com.example.demo.Auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.RateLimit;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/public/auth")
public class AuthController {

    @Autowired
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    

    @PostMapping("/login")
    @RateLimit(limit = 10, period = 60)
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest loginRequest) {
        
        return authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
    
    }


    @PostMapping("/refresh")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> logout(HttpServletResponse response, @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        return authService.logout(response, refreshToken);
    }
    
    @DeleteMapping(path = "/{id}")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id, @CookieValue(value="refreshToken", required=false) String refreshToken, HttpServletResponse response){
        return authService.deleteUserById(id, refreshToken, response);
    }

    @PutMapping(path = "{id}")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId, @RequestBody updateUserDTO dto, HttpServletResponse response, @CookieValue(value="refreshToken", required=false) String refreshToken) {
        
        return authService.updateUser(userId, dto.name(), dto.email(), dto.password(), response, refreshToken);
    }

    public record updateUserDTO(String name, String email, String password){

    }


}
