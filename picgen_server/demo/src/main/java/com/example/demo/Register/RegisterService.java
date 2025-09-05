package com.example.demo.Register;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.User.User;
import com.example.demo.User.UserRepository;

@Service
public class RegisterService {
    
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public RegisterResponse registerUser(RegisterRequest registerRequest){

        Optional<User> user = userRepository.findUserByEmail(registerRequest.getEmail());
        
        if(user.isPresent()){
            return new RegisterResponse(false, "user already exists");
        }
        else {
            String hash = passwordEncoder.encode(registerRequest.getPassword());
            String email = registerRequest.getEmail();
            String name = registerRequest.getName();
            User newUser = new User(name, email, hash);

            userRepository.save(newUser);
            
            return new RegisterResponse(true, "user registered");
        }
    }

}
