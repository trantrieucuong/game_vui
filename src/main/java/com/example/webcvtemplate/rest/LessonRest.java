package com.example.webcvtemplate.rest;

import com.example.webcvtemplate.entity.Lesson;
import com.example.webcvtemplate.model.request.LessonRequest;
import com.example.webcvtemplate.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonRest {
    private final LessonService lessonService;
    // Lấy danh sách tất cả bài học
    @GetMapping
    public ResponseEntity<List<Lesson>> getAllLessons() {
        List<Lesson> lessons = lessonService.getAllLessons();
        return ResponseEntity.ok(lessons); // status code 200
    }

    // Lấy bài học theo mã
    @GetMapping("/{lessonCode}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable String lessonCode) {
        Lesson lesson = lessonService.getLessonByLessonCode(lessonCode);
        return ResponseEntity.ok(lesson); // status code 200
    }

    // Tạo bài học mới
    @PostMapping
    public ResponseEntity<Lesson> createLesson(@RequestBody LessonRequest request) {
        Lesson lesson = lessonService.createLesson(request);
        return ResponseEntity.ok(lesson); // status code 200
    }

    // Cập nhật bài học
    @PutMapping("/{lessonCode}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable String lessonCode, @RequestBody LessonRequest request) {
        Lesson lesson = lessonService.updateLesson(lessonCode, request);
        return ResponseEntity.ok(lesson); // status code 200
    }

    // Xóa bài học
    @DeleteMapping("/{lessonCode}")
    public ResponseEntity<Void> deleteLesson(@PathVariable String lessonCode) {
        lessonService.deleteLesson(lessonCode);
        return ResponseEntity.noContent().build(); // status code 204
    }

}
