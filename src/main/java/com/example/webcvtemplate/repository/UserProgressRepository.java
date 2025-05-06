package com.example.webcvtemplate.repository;

import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.entity.UserProgress;
import com.example.webcvtemplate.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress,String> {

    Optional<UserProgress> findByUserAndVideo(User user, Video video);


}
