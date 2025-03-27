package com.example.webcvtemplate.rest;

import com.example.webcvtemplate.entity.Video;
import com.example.webcvtemplate.model.request.VideoRequest;
import com.example.webcvtemplate.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoRest {
    private final VideoService videoService;

    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @GetMapping("/{videoCode}")
    public ResponseEntity<Video> getVideoByCode(@PathVariable String videoCode) {
        return ResponseEntity.ok(videoService.getVideoByVideoCode(videoCode));
    }

    @PostMapping("/upload/{lessonCode}")
    public ResponseEntity<?> handleUploadVideo(
            @PathVariable String lessonCode,
            @RequestParam("file") MultipartFile file) {

        try {
            Video video = videoService.uploadVideo(lessonCode, file);
            return ResponseEntity.ok(video);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải lên video: " + e.getMessage());
        }
    }
    @PutMapping("/{videoCode}")
    public ResponseEntity<Video> updateVideo(
            @PathVariable String videoCode,
            @RequestBody VideoRequest request) {
        return ResponseEntity.ok(videoService.updateVideo(videoCode, request));
    }

    @DeleteMapping("/{videoCode}")
    public ResponseEntity<Void> deleteVideo(@PathVariable String videoCode) {
        videoService.deleteVideo(videoCode);
        return ResponseEntity.noContent().build();
    }
}
