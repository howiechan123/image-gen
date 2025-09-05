package com.example.demo.Pictures;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class PictureService {
    
    private final PictureRepository pictureRepository;

    @Autowired
    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    public List<Picture> getPicturesById(Long userId){
        return pictureRepository.findPicturesByUserId(userId);
    }

    public List<Picture> getPictures() {
        return pictureRepository.findAllPictures();
    }


    public void addPicture(Picture picture) {
        pictureRepository.save(picture);
        System.out.println("picture saved");
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

    



    
}
