package com.example.demo.Pictures;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.User.User;
import com.example.demo.User.UserService;

@RestController
@RequestMapping(path="api/Pictures")
public class PictureController {
    
    private final PictureService pictureService;
    private final UserService userService;
    @Autowired
    public PictureController(PictureService pictureService, UserService userService) {
        this.pictureService = pictureService;
        this.userService = userService;
    }

    @GetMapping
    public List<Picture> getPictures(){
        return pictureService.getPictures();
    }


    @GetMapping("/user")
    public ResponseEntity<?> getPicturesByUser() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = Long.valueOf(userDetails.getUsername());
        return pictureService.getPicturesById(userId);
        
    }


    @PostMapping(path="/save")
    public ResponseEntity<?> savePicture(@RequestBody pictureRequestDTO dto) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = Long.valueOf(userDetails.getUsername());
        User user = new User(userId);
        
        return pictureService.addPicture(dto, user);
    }

    @DeleteMapping(path="{pictureId}")
    public void deletePicture(@PathVariable("pictureId") Long pictureId){
        pictureService.deletePicture(pictureId);
    }

    @PutMapping(path="{pictureId}")
    public void changePictureName(@PathVariable("pictureId") Long pictureId, @RequestBody changeNameDTO dto){
        pictureService.changePictureName(pictureId, dto.newName);
    }

    // @GetMapping(path ="{pictureId}")
    // public void downloadPicture(@PathVariable("pictureId") Long pictureId) {

    // }

    public record pictureRequestDTO(String fileName, String filePath) {

    }

    public record changeNameDTO(String newName){

    }

    
    
}
