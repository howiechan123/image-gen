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

    public boolean deleteUserById(Long id){
        boolean exists = userRepository.existsById(id);
        
        if(!exists) {
            throw new IllegalStateException("user " + id + " does not exist");
        }
        userRepository.deleteById(id);
        System.out.println("user deleted");
        return exists;
    }

    @Transactional
    public ResponseEntity<?> updateUser(Long userId, String name, String email, String password) {
        
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("user id does not exist"));

        if(name != null && name.length() > 0){
            user.setName(name);
        }
        if(email != null && email.length() > 0){
            user.setEmail(email);
        }
        if(password != null && password.length() > 0){
            user.setPassword(password);
        }

        

        userDTO dto = new userDTO(user.getId(), user.getName(), user.getEmail());
        System.out.println(dto);
        return ResponseEntity.ok(new userResponse(dto, "User updated", true));
    }
    
    public record userResponse(userDTO dto, String message, boolean success){

    }
    public record userDTO(Long id, String name, String email) {

    }

}
