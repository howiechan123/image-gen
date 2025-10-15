package com.example.demo.Auth;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;

import com.example.demo.JWT.JWTUtil;
import com.example.demo.User.User;
import com.example.demo.User.UserRepository;
import com.example.demo.User.UserService.userResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, JWTUtil jwtUtil, BlacklistedTokenRepository blacklistRepo, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.blacklistRepo = blacklistRepo;
        this.passwordEncoder = passwordEncoder;
    }
    

    //logout
    public void blacklistRefreshToken(String refreshToken, Date expiry) {
        BlacklistedToken token = new BlacklistedToken(refreshToken, expiry);
        blacklistRepo.save(token);
    }

    //refresh
    private boolean isBlacklisted(String refreshToken) {
        return blacklistRepo.existsByToken(refreshToken);
    }

    public ResponseEntity<?> authenticate(String email, String password) {
        User user = userRepository.findUserByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }

        
        String accessToken = jwtUtil.generateToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getTokenVersion());

        
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // set false if testing locally without https
                .path("/public/auth")
                .maxAge(60 * 60 * 5) // 5 hours
                .sameSite("Lax")
                .build();

        userDTO userDto = new userDTO(user.getId(), user.getName(), user.getEmail());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new authResponse(userDto, "Login Success", true, accessToken));
    }



   public ResponseEntity<?> refresh(String refreshToken) {
        if (refreshToken == null || isBlacklisted(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        try {
            String userId = jwtUtil.extractUserId(refreshToken);
            Integer tokenVersionFromToken = jwtUtil.extractTokenVersion(refreshToken);

            if (userId == null || tokenVersionFromToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            User user = userRepository.findUserById(Long.valueOf(userId)).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            if (!tokenVersionFromToken.equals(user.getTokenVersion())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired or revoked refresh token");
            }

            if (!jwtUtil.validateToken(refreshToken, userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
            }

            user.setTokenVersion(user.getTokenVersion() + 1);
            userRepository.save(user);

            String newAccessToken = jwtUtil.generateToken(Long.valueOf(userId));
            String newRefreshToken = jwtUtil.generateRefreshToken(Long.valueOf(userId), user.getTokenVersion());


            ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                    .httpOnly(true)
                    .secure(true) // set false if testing locally without https
                    .path("/public/auth")
                    .maxAge(60 * 60 * 5)
                    .sameSite("Lax")
                    .build();

            userDTO dto = new userDTO(user.getId(), user.getName(), user.getEmail());
            authResponse response = new authResponse(dto, "Token refreshed", true, newAccessToken);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);

        } catch (Exception e) {
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
            .secure(true)
            .path("/public/auth")
            .maxAge(0)
            .sameSite("Lax")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logged out successfully");
    }

    
    public ResponseEntity<?> deleteUserById(Long id, String refreshToken, HttpServletResponse response){
        boolean exists = userRepository.existsById(id);
        
        if(!exists) {
            throw new IllegalStateException("user " + id + " does not exist");
        }

        if (refreshToken != null) {
            java.util.Date expiry = jwtUtil.extractClaim(refreshToken, Claims::getExpiration);
            blacklistRepo.save(new BlacklistedToken(refreshToken, new java.sql.Date(expiry.getTime())));
        }

        // Clear cookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .path("/public/auth")
            .maxAge(0)
            .sameSite("Lax")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        userRepository.deleteById(id);
        System.out.println("user deleted");

        return ResponseEntity.ok("Deleted account successfully");
    }


    @Transactional
    public ResponseEntity<?> updateUser(Long userId, String name, String email, String password, HttpServletResponse response, String refreshToken) {
        
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("user id does not exist"));

        if(name != null && name.length() > 0){
            user.setName(name);
        }
        if(email != null && email.length() > 0){
            user.setEmail(email);
        }
        if(password != null && password.length() > 0){

        String newHash = passwordEncoder.encode(password);
        user.setPassword(newHash);

            if (refreshToken != null) {
            java.util.Date expiry = jwtUtil.extractClaim(refreshToken, Claims::getExpiration);
            blacklistRepo.save(new BlacklistedToken(refreshToken, new java.sql.Date(expiry.getTime())));
            }

            // Clear cookie
            ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/public/auth")
                .maxAge(0)
                .sameSite("Lax")
                .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        }


        userDTO dto = new userDTO(user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(new userResponse(dto, "User updated", true));
    }

    public record authResponse(userDTO user, String message, boolean success, String token) {

    };
    
    public record userDTO(Long id, String name, String email) {
    
    };

    public record userResponse(userDTO dto, String message, boolean success){

    };
}
