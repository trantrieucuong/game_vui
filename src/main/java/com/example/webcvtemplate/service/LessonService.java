package com.example.webcvtemplate.service;

import com.example.webcvtemplate.entity.Lesson;
import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.model.request.LessonRequest;
import com.example.webcvtemplate.repository.LessonRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;

    // Lấy danh sách tất cả bài học
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    // Lấy thông tin bài học theo mã bài học
    public Lesson getLessonByLessonCode(String lessonCode) {
        return lessonRepository.findById(lessonCode).orElse(null);
    }
    @Transactional
    public Lesson createLesson(LessonRequest lessonRequest) {
        Lesson lesson = new Lesson();
        lesson.setLessonCode(generateLessonCode()); // Tạo mã bài học tự động
        lesson.setLessonTitle(lessonRequest.getLessonTitle());
        lesson.setDescription(lessonRequest.getDescription());
        lesson.setStatus(lessonRequest.isStatus());

        return lessonRepository.save(lesson);
    }

    // Cập nhật bài học
    @Transactional
    public Lesson updateLesson(String lessonCode, LessonRequest lessonRequest) {
        Optional<Lesson> optionalLesson = lessonRepository.findById(lessonCode);
        if (optionalLesson.isEmpty()) {
            throw new RuntimeException("Bài học không tồn tại!");
        }
        Lesson lesson = optionalLesson.get();
        lesson.setDescription(lessonRequest.getDescription());
        lesson.setStatus(lessonRequest.isStatus());

        return lessonRepository.save(lesson);
    }

    // Xóa bài học
    @Transactional
    public void deleteLesson(String lessonCode) {
        if (!lessonRepository.existsById(lessonCode)) {
            throw new RuntimeException("Bài học không tồn tại!");
        }
        lessonRepository.deleteById(lessonCode);
    }

    // Hàm tạo mã bài học ngẫu nhiên (vd: LES12345)
    private String generateLessonCode() {
        return "LES" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }
}
