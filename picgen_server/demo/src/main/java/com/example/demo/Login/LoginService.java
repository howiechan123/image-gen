package com.example.demo.Login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.example.demo.JWT.JWTUtil;
import com.example.demo.User.User;
import com.example.demo.User.UserRepository;

@Service
public class LoginService {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public LoginService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> authenticate(String email, String password) {
        User user = userRepository.findUserByEmail(email).orElse(null);

        if(user == null){
            return ResponseEntity.status(401).body("Invalid Credentials");
        }
        boolean match = BCrypt.checkpw(password, user.getPassword());

        if (match) {
            String token = jwtUtil.generateToken(email);
            userDTO userDto = new userDTO(user.getId(), user.getName(), user.getEmail());
            return ResponseEntity.ok(new loginResponse(userDto, "Login Success", true, token));
        } else {
            return ResponseEntity.status(401).body("Invalid Credentials");
        }

    }
    public record loginResponse(userDTO user, String message, boolean success, String token) {

    };
    
    public record userDTO(Long id, String name, String email) {
    
    };
}
