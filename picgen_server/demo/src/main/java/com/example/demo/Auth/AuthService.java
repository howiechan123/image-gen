package com.example.demo.Auth;

import org.springframework.http.*;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.Cookie;

import com.example.demo.JWT.JWTUtil;
import com.example.demo.User.User;
import com.example.demo.User.UserRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistRepo;

    @Autowired
    public AuthService(UserRepository userRepository, JWTUtil jwtUtil, BlacklistedTokenRepository blacklistRepo) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.blacklistRepo = blacklistRepo;
    }
    

    // Call this on logout
    public void blacklistRefreshToken(String refreshToken, Date expiry) {
        BlacklistedToken token = new BlacklistedToken(refreshToken, expiry);
        blacklistRepo.save(token);
    }

    // Call this in refresh endpoint
    private boolean isBlacklisted(String refreshToken) {
        return blacklistRepo.existsByToken(refreshToken);
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
                .path("/public/auth")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Strict")
                .build();

        userDTO userDto = new userDTO(user.getId(), user.getName(), user.getEmail());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new loginResponse(userDto, "Login Success", true, accessToken));
    }



    public ResponseEntity<?> refresh(String refreshToken) {
        System.out.println("Refresh token" + refreshToken);
        if (refreshToken == null || isBlacklisted(refreshToken)) {
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 1");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        try {
            String email = jwtUtil.extractEmail(refreshToken);

            if (!jwtUtil.validateToken(refreshToken, email)) {
                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 2");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 8");
            String newAccessToken = jwtUtil.generateToken(email);
            return ResponseEntity.ok(new loginResponse(null, "Token refreshed", true, newAccessToken));

        } catch (Exception e) {
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 3");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }
    }

    public ResponseEntity<?> logout(HttpServletResponse response, @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken != null) {
            java.util.Date expiry = jwtUtil.extractClaim(refreshToken, Claims::getExpiration);
            blacklistRepo.save(new BlacklistedToken(refreshToken, new java.sql.Date(expiry.getTime())));

        }

        // Clear cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(false)
            .path("/public/auth")
            .maxAge(0)
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
