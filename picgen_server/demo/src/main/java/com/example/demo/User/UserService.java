package com.example.demo.User;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;



import jakarta.transaction.Transactional;

@Service
public class UserService {
    
    
    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    public Long getUserIdByEmail(String email){
        return userRepository.findUserIdByEmail(email);
    }


    public void addUser(User user){
        
        Optional<User> userByEmail = userRepository.findUserByEmail(user.getEmail());
        
        if(user.getEmail() == null || user.getId() == null || user.getName() == null || user.getPassword() == null){
            throw new IllegalStateException("missing data field");
        }

        if(userByEmail.isPresent()) {
            throw new IllegalStateException("email already exists");
        }
        else{
            userRepository.save(user);
            System.out.println("User added to database");
        }
        
    }


    
    public record userResponse(userDTO dto, String message, boolean success){

    }
    public record userDTO(Long id, String name, String email) {

    }

}
