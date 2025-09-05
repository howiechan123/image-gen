package com.example.demo.Pictures;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PictureRepository extends JpaRepository<Picture, Long>{
    @Query("SELECT DISTINCT p FROM Picture p LEFT JOIN FETCH p.user")
    List<Picture> findAllPictures();
    List<Picture> findByUserId(Long userId);

    @Query("SELECT p FROM Picture p WHERE p.user.id = ?1")
    List<Picture> findPicturesByUserId(Long userId);
}
