package com.example.webcvtemplate.repository;

import com.example.webcvtemplate.entity.QuestionVideo;
import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, String> {
    UserAnswer findByUserAndQuestionVideo(User user, QuestionVideo questionVideo);
}
