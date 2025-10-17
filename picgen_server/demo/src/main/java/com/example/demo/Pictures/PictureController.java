package com.example.demo.Pictures;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.User.User;
import com.example.demo.User.UserService;
import com.example.demo.config.RateLimit;
import com.fasterxml.jackson.annotation.JsonProperty;

import reactor.core.publisher.Mono;

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
    @RateLimit(limit = 50, period = 60)
    public List<Picture> getPictures(){
        return pictureService.getPictures();
    }


    @GetMapping("/user")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> getPicturesByUser() {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = Long.valueOf(userDetails.getUsername());
        return pictureService.getPicturesById(userId);
        
    }


    @PostMapping(path="/save")
    @RateLimit(limit = 50, period = 60)
    public ResponseEntity<?> savePicture(@RequestBody pictureRequestDTO dto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = Long.valueOf(userDetails.getUsername());
        User user = new User(userId);
        
        return pictureService.addPicture(dto, user);
    }

    @DeleteMapping(path="{pictureId}")
    @RateLimit(limit = 50, period = 60)
    public void deletePicture(@PathVariable("pictureId") Long pictureId){
        pictureService.deletePicture(pictureId);
    }

    @PutMapping(path="{pictureId}")
    @RateLimit(limit = 50, period = 60)
    public void changePictureName(@PathVariable("pictureId") Long pictureId, @RequestBody changeNameDTO dto){
        pictureService.changePictureName(pictureId, dto.newName);
    }

    @PostMapping("/generate_image")
    @RateLimit(limit = 5, period = 60)
    public Mono<ResponseEntity<?>> generateImage(@RequestBody promptDTO dto){
        return pictureService.generateImage(dto.prompt(), dto.dimensions(), dto.inferenceSteps(), dto.guidanceScale());
    }

    @PostMapping("/pollHF")
    @RateLimit(limit = 10, period = 60)
    public CompletableFuture<ResponseEntity<?>> poll(@RequestBody eventDTO dto) {
        String eventId = dto.event_id();
        return pictureService.pollHF(eventId);
    }

    public record pictureRequestDTO(String fileName, String filePath) {

    }

    public record changeNameDTO(String newName){

    }

    public record promptDTO(String prompt, int dimensions, @JsonProperty("inference_steps") int inferenceSteps, @JsonProperty("guidance_scale") int guidanceScale){

    }

    public record eventDTO(String event_id){}

    
    
}
