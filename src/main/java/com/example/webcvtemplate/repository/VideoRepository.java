package com.example.webcvtemplate.repository;

import com.example.webcvtemplate.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {
    List<Video> findByLesson_LessonCode(String lessonCode);
}
