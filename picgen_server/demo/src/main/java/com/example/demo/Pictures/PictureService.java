package com.example.demo.Pictures;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Pictures.PictureController.pictureRequestDTO;
import com.example.demo.User.User;

import jakarta.transaction.Transactional;

@Service
public class PictureService {
    
    private final PictureRepository pictureRepository;

    @Autowired
    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
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
        Picture picture = new Picture(dto.fileName(), dto.filePath(), user);
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

    

    public record pictureResponse(pictureDTO dto, String message, boolean success) {

    };

     public record pictureDTO(List<Picture> pics){

    };

    
}
