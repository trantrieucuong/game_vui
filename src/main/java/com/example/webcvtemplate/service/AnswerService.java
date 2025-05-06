package com.example.webcvtemplate.service;

import com.example.webcvtemplate.entity.QuestionVideo;
import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.entity.UserAnswer;
import com.example.webcvtemplate.entity.Video;
import com.example.webcvtemplate.repository.AnswerRepository;
import com.example.webcvtemplate.repository.QuestionRepository;
import com.example.webcvtemplate.repository.UserRepository;
import com.example.webcvtemplate.repository.VideoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
@Slf4j
@Service
public class AnswerService {
    public static String generateAnswerCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        return "ANS" + String.format("%04d", randomNumber);  // Đảm bảo mã có 4 chữ số sau tiền tố "ANS"
    }
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionVideoRepository;
    private final VideoRepository videoRepository;

    public AnswerService(AnswerRepository answerRepository, UserRepository userRepository,
                         QuestionRepository questionVideoRepository, VideoRepository videoRepository) {
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.questionVideoRepository = questionVideoRepository;
        this.videoRepository = videoRepository;
    }

//    @Transactional
//    public UserAnswer saveUserAnswer(String questionCode, String userCode, char selectedOption, boolean isCorrect) {
//        // Lấy câu hỏi và người dùng từ cơ sở dữ liệu
//        QuestionVideo questionVideo = questionVideoRepository.findByQuestionCode(questionCode);
//        User user = userRepository.getUserByCode(userCode);
//
//        if (questionVideo == null || user == null) {
//            throw new IllegalArgumentException("Question or User not found");
//        }
//
//        // Tạo mới đối tượng UserAnswer
//        UserAnswer userAnswer = new UserAnswer();
//        userAnswer.setQuestionVideo(questionVideo);
//        userAnswer.setUser(user);
//        userAnswer.setSelectedOption(selectedOption);
//        userAnswer.setIsCorrect(isCorrect);
//        userAnswer.setAnsweredAt(LocalDateTime.now());
//
//        // Lưu câu trả lời vào cơ sở dữ liệu
//        return answerRepository.save(userAnswer);
//    }
//


//    @Transactional
//    public List<UserAnswer> getAnswersByVideoCode(String videoCode) {
//        // Lấy video từ cơ sở dữ liệu
//        Video video = videoRepository.findFirstByVideoCode(videoCode);
//
//        if (video == null) {
//            throw new IllegalArgumentException("Video not found");
//        }
//
//        // Tìm và trả về tất cả câu trả lời liên quan đến video
//        return answerRepository.findByVideo(video);
//    }
}
