package com.example.webcvtemplate.repository;
import com.example.webcvtemplate.entity.QuestionVideo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionVideo, String> {
    List<QuestionVideo> findByVideo_VideoCode(String questionCode);
}
