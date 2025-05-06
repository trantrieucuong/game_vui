package com.example.webcvtemplate.rest;

import com.example.webcvtemplate.entity.*;
import com.example.webcvtemplate.model.request.AnswerRequest;
import com.example.webcvtemplate.model.responser.AnswerResponse;
import com.example.webcvtemplate.repository.*;
import com.example.webcvtemplate.service.QuestionService;
import com.example.webcvtemplate.service.UserService;
import com.example.webcvtemplate.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.webcvtemplate.service.AnswerService.generateAnswerCode;

@RestController
@RequestMapping("/api/questions")
public class QuestionVideoRest {
    @Autowired
    private QuestionService questionVideoService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAnswerRepository userAnswerRepository;
    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // API nhận câu trả lời từ client và kiểm tra tính đúng sai
    @PostMapping("/submit-answer")
    public Map<String, Object> submitAnswerAp(@RequestParam String videoCode,
                                              @RequestParam Map<String, String> answers) {
        Map<String, Object> response = new HashMap<>();

        // Tìm video
        Video video = videoRepository.findById(videoCode).orElse(null);
        if (video == null) {
            response.put("status", "error");
            response.put("message", "Video không tồn tại.");
            return response;
        }

        // Tìm user mặc định
        Optional<User> userOpt = userRepository.findByUserCode("us7732");
        if (!userOpt.isPresent()) {
            response.put("status", "error");
            response.put("message", "Không tìm thấy người dùng với mã 'us7732'.");
            return response;
        }
        User user = userOpt.get();

        int correct = 0;
        int total = 0;

        // Lấy danh sách các câu hỏi (forcedVideoCode ép cứng)
        String forcedVideoCode = "vdc8ab9b18";
        List<QuestionVideo> questionVideos = questionRepository.findByVideo_VideoCode(forcedVideoCode);

        for (QuestionVideo questionVideo : questionVideos) {
            String userAnswer = answers.get("answers[" + questionVideo.getQuestionCode() + "]");
            if (userAnswer != null) {
                total++;

                boolean isCorrect = userAnswer.equalsIgnoreCase(String.valueOf(questionVideo.getCorrectOption()));
                if (isCorrect) {
                    correct++;
                }

                // ➡️ Check xem đã có câu trả lời chưa
                UserAnswer existingAnswer = userAnswerRepository.findByUserAndQuestionVideo(user, questionVideo);

                if (existingAnswer != null) {
                    // ➡️ Nếu có rồi, update
                    existingAnswer.setSelectedOption(userAnswer.charAt(0));
                    existingAnswer.setIsCorrect(isCorrect);
                    existingAnswer.setAnsweredAt(LocalDateTime.now());
                    userAnswerRepository.save(existingAnswer);
                } else {
                    // ➡️ Nếu chưa có, tạo mới
                    UserAnswer userAnswerEntity = new UserAnswer();
                    userAnswerEntity.setUser(user);
                    userAnswerEntity.setQuestionVideo(questionVideo);
                    userAnswerEntity.setSelectedOption(userAnswer.charAt(0));
                    userAnswerEntity.setIsCorrect(isCorrect);
                    userAnswerEntity.setAnsweredAt(LocalDateTime.now());
                    userAnswerEntity.setAnswerCode(generateAnswerCode());
                    userAnswerRepository.save(userAnswerEntity);
                }
            }
        }

        // Check tồn tại UserProgress
        Optional<UserProgress> progressOpt = userProgressRepository.findByUserAndVideo(user, video);
        if (progressOpt.isPresent()) {
            // Nếu đã có progress -> update
            UserProgress existingProgress = progressOpt.get();
            existingProgress.setCompletedAt(LocalDateTime.now());
            existingProgress.setProgressStatus("completed");
            userProgressRepository.save(existingProgress);
        } else {
            // Nếu chưa có progress -> insert mới
            UserProgress newProgress = new UserProgress();
            newProgress.setProgressCode(generateProgressCode());
            newProgress.setUser(user);
            newProgress.setVideo(video);
            newProgress.setCompletedAt(LocalDateTime.now());
            newProgress.setProgressStatus("completed");
            userProgressRepository.save(newProgress);
        }
        // Trả kết quả
        response.put("status", "success");
        response.put("correct", correct);
        response.put("total", total);
        response.put("message", "Cảm ơn bạn đã tham gia!");
        return response;
    }


    // Hàm tạo mã Answer tự động
    private String generateAnswerCode() {
        return "ANS" + String.format("%04d", (int) (Math.random() * 10000));
    }
    private String generateProgressCode() {
        return "PROG" + String.format("%04d", (int) (Math.random() * 10000));
    }



    @PostMapping("/submit-answerLater")
    @ResponseBody
    public Map<String, Object> submitAnswerApi(@RequestParam String videoCode,
                                              @RequestParam Map<String, String> answers) {
        Map<String, Object> response = new HashMap<>();

        // Tìm video theo videoCode truyền vào
        Video video = videoRepository.findById(videoCode).orElse(null);
        if (video == null) {
            response.put("status", "error");
            response.put("message", "Video không tồn tại.");
            return response;
        }

        // Tìm người dùng mặc định
        Optional<User> userOpt = userRepository.findByUserCode("us7732");
        if (!userOpt.isPresent()) {
            response.put("status", "error");
            response.put("message", "Không tìm thấy người dùng với mã 'us7732'.");
            return response;
        }
        User user = userOpt.get();

        int correct = 0;
        int total = 0;

        // Lấy danh sách các câu hỏi của video ép cứng "vde6ef6dd3"
        String forcedVideoCode = "vde6ef6dd3";
        List<QuestionVideo> questionVideos = questionRepository.findByVideo_VideoCode(forcedVideoCode);

        for (QuestionVideo questionVideo : questionVideos) {
            String userAnswer = answers.get("answers[" + questionVideo.getQuestionCode() + "]");
            if (userAnswer != null) {
                total++;

                boolean isCorrect = userAnswer.equalsIgnoreCase(String.valueOf(questionVideo.getCorrectOption()));
                if (isCorrect) {
                    correct++;
                }

                UserAnswer userAnswerEntity = new UserAnswer();
                userAnswerEntity.setUser(user);
                userAnswerEntity.setQuestionVideo(questionVideo);
                userAnswerEntity.setSelectedOption(userAnswer.charAt(0));
                userAnswerEntity.setIsCorrect(isCorrect);
                userAnswerEntity.setAnsweredAt(LocalDateTime.now());
                userAnswerEntity.setAnswerCode(generateAnswerCode());

                userAnswerRepository.save(userAnswerEntity);
            }
        }

        // Tạo tiến trình học
        String progressCode = "PROG" + String.format("%04d", (int) (Math.random() * 10000));
        UserProgress userProgress = new UserProgress();
        userProgress.setProgressCode(progressCode);
        userProgress.setUser(user);
        userProgress.setVideo(video);
        userProgress.setCompletedAt(LocalDateTime.now());
        userProgress.setProgressStatus("completed");

        userProgressRepository.save(userProgress);

        // Trả kết quả
        response.put("status", "success");
        response.put("correct", correct);
        response.put("total", total);
        response.put("message", "Cám ơn bạn đã tham gia!");

        return response;
    }



}
