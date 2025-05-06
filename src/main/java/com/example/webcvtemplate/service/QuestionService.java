package com.example.webcvtemplate.service;

import com.example.webcvtemplate.entity.Question;
import com.example.webcvtemplate.entity.QuestionVideo;
import com.example.webcvtemplate.entity.Video;
import com.example.webcvtemplate.repository.LessonRepository;
import com.example.webcvtemplate.repository.QuestionRepository;
import com.example.webcvtemplate.repository.VideoRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private VideoRepository videoRepository;


    // Kiểm tra câu trả lời đúng hay sai
    public boolean checkAnswer(String questionIndex, String answer) {
        // Tìm câu hỏi theo questionIndex (là String)
        QuestionVideo questionVideo = questionRepository.findById(questionIndex)
                .orElseThrow(() -> new IllegalArgumentException("Câu hỏi không tồn tại"));

        // Kiểm tra câu trả lời
        return questionVideo.getCorrectOption() == answer.charAt(0);
    }


    // Lấy tất cả câu hỏi
    public List<QuestionVideo> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Lấy câu hỏi theo mã
    public Optional<QuestionVideo> getQuestionById(String questionCode) {
        return questionRepository.findById(questionCode);
    }

    public List<QuestionVideo> getQuestionsByVideoCode(String videoCode) {
        return questionRepository.findByVideo_VideoCode(videoCode);
    }

    // 🆕 Sinh mã tự động
    private String generateQuestionCode() {
        String prefix = "Q";
        int nextNumber = (int) (Math.random() * 9000) + 1000; // Số ngẫu nhiên 4 chữ số
        return prefix + nextNumber;
    }

    public List<QuestionVideo> processCSV(MultipartFile file, String videoCode) throws Exception {
        List<QuestionVideo> questions = new ArrayList<>();
        Video video = videoRepository.findByVideoCode(videoCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy video"));

        try (
                Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                CSVReader csvReader = new CSVReader(reader)
        ) {
            String[] row;
            int lineNumber = 0;

            while ((row = csvReader.readNext()) != null) {
                lineNumber++;

                if (lineNumber == 1) continue; // Bỏ qua tiêu đề

                if (row.length < 10) {
                    throw new RuntimeException("Lỗi dòng " + lineNumber + ": Không đủ cột dữ liệu!");
                }

                if (!row[1].trim().isEmpty()) {
                    throw new RuntimeException("Lỗi dòng " + lineNumber + ": Không được nhập mã câu hỏi!");
                }

                QuestionVideo question = new QuestionVideo();
                question.setQuestionCode(generateQuestionCode());
                question.setVideo(video);
                question.setContent(row[3].trim());
                question.setOptionA(row[4].trim());
                question.setOptionB(row[5].trim());
                question.setOptionC(row[6].trim());
                question.setOptionD(row[7].trim());
                question.setCorrectOption(row[8].trim().charAt(0));
                question.setExplanation(row[9].trim());

                questions.add(question);
            }
        }

        return questionRepository.saveAll(questions);
    }


    // 🆕 Thêm mới câu hỏi với mã tự động
    @Transactional
    public QuestionVideo addQuestion(QuestionVideo question) {
        String newCode;
        do {
            newCode = generateQuestionCode();
        } while (questionRepository.existsById(newCode)); // Đảm bảo không trùng

        question.setQuestionCode(newCode);
        return questionRepository.save(question);
    }

    // Cập nhật câu hỏi
    public QuestionVideo updateQuestion(String questionCode, Question updatedQuestion) {
        Optional<QuestionVideo> existingQuestion = questionRepository.findById(questionCode);
        if (existingQuestion.isPresent()) {
            QuestionVideo question = existingQuestion.get();
            question.setContent(updatedQuestion.getContent());
            question.setOptionA(updatedQuestion.getOptionA());
            question.setOptionB(updatedQuestion.getOptionB());
            question.setOptionC(updatedQuestion.getOptionC());
            question.setOptionD(updatedQuestion.getOptionD());
            question.setCorrectOption(updatedQuestion.getCorrectOption());
            question.setExplanation(updatedQuestion.getExplanation());

            return questionRepository.save(question);
        }
        return null;
    }
}
