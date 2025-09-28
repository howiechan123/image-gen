package com.example.demo.Login;

import org.springframework.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import com.example.demo.JWT.JWTUtil;
import com.example.demo.User.User;
import com.example.demo.User.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginService {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public LoginService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // public ResponseEntity<?> authenticate(String email, String password) {
    //     User user = userRepository.findUserByEmail(email).orElse(null);

    //     if(user == null){
    //         return ResponseEntity.status(401).body("Invalid Credentials");
    //     }
    //     boolean match = BCrypt.checkpw(password, user.getPassword());

    //     if (match) {
    //         String token = jwtUtil.generateToken(email);
    //         String refreshToken = jwtUtil.generateRefreshToken(email);
    //         userDTO userDto = new userDTO(user.getId(), user.getName(), user.getEmail());
    //         return ResponseEntity.ok(new loginResponse(userDto, "Login Success", true, token));
    //     } else {
    //         return ResponseEntity.status(401).body("Invalid Credentials");
    //     }

    // }
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




    public record loginResponse(userDTO user, String message, boolean success, String token) {

    };
    
    public record userDTO(Long id, String name, String email) {
    
    };
}
