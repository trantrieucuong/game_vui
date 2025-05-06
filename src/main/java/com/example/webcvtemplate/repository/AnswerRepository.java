package com.example.webcvtemplate.repository;

import com.example.webcvtemplate.entity.UserAnswer;
import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.entity.QuestionVideo;
import com.example.webcvtemplate.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<UserAnswer, String> {

    // Tìm câu trả lời theo mã câu hỏi và người dùng
    UserAnswer findByQuestionVideoAndUser(QuestionVideo questionVideo, User user);

//    // Tìm câu trả lời theo mã video
//    List<UserAnswer> findByVideo(Video video);
}
