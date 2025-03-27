package com.example.webcvtemplate.service;

import com.example.webcvtemplate.entity.Lesson;
import com.example.webcvtemplate.entity.Video;
import com.example.webcvtemplate.model.request.VideoRequest;
import com.example.webcvtemplate.repository.LessonRepository;
import com.example.webcvtemplate.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final LessonRepository lessonRepository;

    @Value("${video.upload.path}") // Đọc đường dẫn từ application.properties
    private String videoUploadPath;

    public VideoService(VideoRepository videoRepository, LessonRepository lessonRepository) {
        this.videoRepository = videoRepository;
        this.lessonRepository = lessonRepository;
    }

    // Lấy danh sách tất cả video
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    // Lấy video theo mã
    public Video getVideoByVideoCode(String videoCode) {
        return videoRepository.findById(videoCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy video với mã: " + videoCode));
    }

    public List<Video> getVideosByLessonCode(String lessonCode) {
        return videoRepository.findByLesson_LessonCode(lessonCode);
    }

    // Tải lên video mới
    public Video uploadVideo(String lessonCode, MultipartFile file) throws IOException {
        // Kiểm tra bài học có tồn tại không
        Lesson lesson = lessonRepository.findById(lessonCode)
                .orElseThrow(() -> new IllegalArgumentException("Bài học không tồn tại!"));

        // Tạo mã video ngẫu nhiên
        String videoCode = "vd" + UUID.randomUUID().toString().substring(0, 8);

        // Định dạng đường dẫn video
        String fileName = videoCode + ".mp4";
        Path videoPath = Paths.get(videoUploadPath, fileName);

        // Tạo thư mục nếu chưa tồn tại
        File uploadDir = new File(videoUploadPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IOException("Không thể tạo thư mục lưu trữ video!");
        }

        // Lưu file vào thư mục
        Files.write(videoPath, file.getBytes());

        // Lấy thời lượng video
        int duration = getVideoDuration(videoPath.toString());

        // Tạo đường dẫn lưu vào database
        String videoUrl = "/video/" + fileName;

        // Lưu thông tin video vào database
        Video video = new Video();
        video.setVideoCode(videoCode);
        video.setLesson(lesson);
        video.setVideoTitle("Tiêu đề video"); // Chưa nhập tiêu đề, để trống
        video.setVideoUrl(videoUrl);
        video.setDurationMinutes(duration);


        return videoRepository.save(video);
    }



    private int getVideoDuration(String videoPath) {
        try {

            ProcessBuilder builder = new ProcessBuilder("/opt/homebrew/bin/ffmpeg", "-i", videoPath);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration:")) {
                    String duration = line.split("Duration: ")[1].split(",")[0].trim();
                    String[] timeParts = duration.split(":");
                    int hours = Integer.parseInt(timeParts[0]);
                    int minutes = Integer.parseInt(timeParts[1]);
                    int seconds = (int) Math.floor(Double.parseDouble(timeParts[2]));
                    return hours * 3600 + minutes * 60 + seconds;
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Cập nhật thông tin video
    public Video updateVideo(String videoCode, VideoRequest request) {
        Video video = videoRepository.findById(videoCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy video để cập nhật!"));

        if (request.getVideoTitle() != null) {
            video.setVideoTitle(request.getVideoTitle());
        }
        if (request.getDurationMinutes() != null) {
            video.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getHasHumor() != null) {
            video.setHasHumor(request.getHasHumor());
        }

        return videoRepository.save(video);
    }

    // Xóa video
    public void deleteVideo(String videoCode) {
        Video video = videoRepository.findById(videoCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy video để xóa!"));

        // Xóa file video thực tế
        Path videoPath = Paths.get(videoUploadPath, video.getVideoUrl().replace("/video/", ""));
        try {
            Files.deleteIfExists(videoPath);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xóa file video!", e);
        }

        // Xóa khỏi database
        videoRepository.delete(video);
    }
}
