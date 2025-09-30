package com.example.demo.Auth;

import org.springframework.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.Cookie;
import com.example.demo.JWT.JWTUtil;
import com.example.demo.User.User;
import com.example.demo.User.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> authenticate(String email, String password) {
        User user = userRepository.findUserByEmail(email).orElse(null);

        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }

        
        String accessToken = jwtUtil.generateToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // set false if testing locally without https
                .path("/public/login/refresh")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Strict")
                .build();

        userDTO userDto = new userDTO(user.getId(), user.getName(), user.getEmail());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new loginResponse(userDto, "Login Success", true, accessToken));
    }


    public ResponseEntity<?> refresh(String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }

        try {
            String email = jwtUtil.extractEmail(refreshToken);

            if (!jwtUtil.validateToken(refreshToken, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            String newAccessToken = jwtUtil.generateToken(email);
            return ResponseEntity.ok(new loginResponse(null, "Token refreshed", true, newAccessToken));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }
    }

    @PostMapping("/public/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        // Clear refresh token cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(false)   // keep true in production (HTTPS)
            .path("/public/login/refresh")
            .maxAge(0)      // expires immediately
            .sameSite("Strict")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }





    public record loginResponse(userDTO user, String message, boolean success, String token) {

    };
    
    public record userDTO(Long id, String name, String email) {
    
    };
}
