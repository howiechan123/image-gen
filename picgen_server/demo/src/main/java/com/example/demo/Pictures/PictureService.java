package com.example.demo.Pictures;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Pictures.PictureController.pictureRequestDTO;
import com.example.demo.User.User;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
public class PictureService {
    
    private final PictureRepository pictureRepository;
    private final ImgBBService imgbbService;
    private final StableDiffusionService stableDiffusionService;

    @Autowired
    public PictureService(PictureRepository pictureRepository, ImgBBService imgbbService, StableDiffusionService stableDiffusionService) {
        this.pictureRepository = pictureRepository;
        this.imgbbService = imgbbService;
        this.stableDiffusionService = stableDiffusionService;
    }

    public ResponseEntity<?> getPicturesById(Long userId){

        List<Picture> pics = pictureRepository.findPicturesByUserId(userId);
        pictureDTO dto = new pictureDTO(pics);
        return ResponseEntity.ok(new pictureResponse(dto, "Get pictures success", true));
    }

    public List<Picture> getPictures() {
        return pictureRepository.findAllPictures();
    }


    public ResponseEntity<?> addPicture(pictureRequestDTO dto, User user) {

        ResponseEntity<?> response = imgbbService.uploadImage(dto.filePath());
        ImgBBService.imgBBResponse responseBody = (ImgBBService.imgBBResponse) response.getBody();


        Picture picture = new Picture(dto.fileName(), responseBody.dto().filePath(), user, responseBody.dto().deleteUrl());
        pictureRepository.save(picture);
        //improve later for errors
        return ResponseEntity.ok("Picture Saved");
    }

    public void deletePicture(Long pictureId) {
       boolean exists = pictureRepository.existsById(pictureId);

       if(exists){
        pictureRepository.deleteById(pictureId);
        System.out.println("picture deleted");
       }
       else {
        throw new IllegalStateException("picture does not exist");
       }
    }

    @Transactional
    public void changePictureName(Long pictureId, String fileName) {
        
        Picture picture = pictureRepository.findById(pictureId).orElseThrow(() -> new IllegalStateException("picture does not exist"));

        picture.setFileName(fileName);
        System.out.println("file name changed");
    }

    public Mono<ResponseEntity<?>> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale){
        return stableDiffusionService.generateImage(prompt, dimensions, inference_steps, guidance_scale);
    }

    public CompletableFuture<ResponseEntity<?>> pollHF(String eventId){
        return stableDiffusionService.pollHF(eventId);
    }

    public record pictureResponse(pictureDTO dto, String message, boolean success) {

    };

    public record pictureDTO(List<Picture> pics){

    };

    
}
