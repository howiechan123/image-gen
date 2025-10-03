package com.example.demo.User;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path= "api/Users")
public class UserController {
    
    @Autowired
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/email")
    public Optional<User> getUserByEmail(@RequestParam("email") String email){
        return userService.getUserByEmail(email);
    }

    @PostMapping
    public void postUser(@RequestBody User user){
        userService.addUser(user);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){
        boolean isDeleted = userService.deleteUserById(id);

        if(isDeleted){
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId, @RequestBody updateUserDTO dto) {
        
        return userService.updateUser(userId, dto.name, dto.email, dto.password);
    }

    public record updateUserDTO(String name, String email, String password){

    }
}
