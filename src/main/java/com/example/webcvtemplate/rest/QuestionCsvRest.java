package com.example.webcvtemplate.rest;

import com.example.webcvtemplate.entity.Lesson;
import com.example.webcvtemplate.entity.QuestionVideo;
import com.example.webcvtemplate.repository.LessonRepository;
import com.example.webcvtemplate.repository.QuestionRepository;
import com.example.webcvtemplate.service.QuestionService;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class QuestionCsvRest {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private QuestionService questionService;


    // 📥 Tải file CSV mẫu
    @GetMapping("/download-csv-template")
    public ResponseEntity<byte[]> downloadCsvTemplate(@RequestParam("video_code") String videoCode) {
        List<QuestionVideo> questions = questionRepository.findByVideo_VideoCode(videoCode);

        StringBuilder csvContent = new StringBuilder();
        // Tiêu đề file CSV
        csvContent.append("action,question_code,video_code,content,option_a,option_b,option_c,option_d,correct_option,explanation\n");

        for (QuestionVideo question : questions) {
            csvContent.append(String.format(
                    "update,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    escapeCsv(question.getQuestionCode()),
                    escapeCsv(question.getVideo().getVideoCode()),
                    escapeCsv(question.getContent()),
                    escapeCsv(question.getOptionA()),
                    escapeCsv(question.getOptionB()),
                    escapeCsv(question.getOptionC()),
                    escapeCsv(question.getOptionD()),
                    escapeCsv(String.valueOf(question.getCorrectOption())),
                    escapeCsv(question.getExplanation() != null ? question.getExplanation() : "")
            ));
        }

        if (questions.isEmpty()) {
            // Nếu không có câu hỏi nào thì tải file mẫu
            csvContent.append(String.format(
                    "add,QV001,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    escapeCsv(videoCode),
                    escapeCsv("Câu hỏi mẫu"),
                    escapeCsv("A"),
                    escapeCsv("B"),
                    escapeCsv("C"),
                    escapeCsv("D"),
                    escapeCsv("A"),
                    escapeCsv("Giải thích mẫu")
            ));
        }

        byte[] fileContent = csvContent.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=question_template.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    // Hàm xử lý escape cho CSV
    private String escapeCsv(String value) {
        if (value == null) return "";
        // Escape dấu " bằng cách thay thế " -> ""
        String escaped = value.replace("\"", "\"\"");
        // Nếu có dấu , hoặc xuống dòng thì bọc bằng dấu ngoặc kép
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }


    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file,
                                       @RequestParam("videoCode") String videoCode) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "File không được để trống!"));
            }

            List<QuestionVideo> questions = questionService.processCSV(file, videoCode);
            return ResponseEntity.ok(Map.of("message", "Tải lên thành công " + questions.size() + " câu hỏi."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }



    // 📤 Import file CSV
    @PostMapping("/import")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File CSV không được để trống!");
        }

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = csvReader.readAll();

            int addedCount = 0, updatedCount = 0;
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                String action = row[0].trim().toLowerCase();
                String questionCode = row[1].trim();

                if (questionCode.isEmpty() || (!action.equals("add") && !action.equals("update"))) {
                    continue;
                }

                Optional<QuestionVideo> existingQuestion = questionRepository.findById(questionCode);

                if (action.equals("add") && existingQuestion.isEmpty()) {
                    QuestionVideo question = createQuestionFromRow(row);
                    questionRepository.save(question);
                    addedCount++;
                } else if (action.equals("update") && existingQuestion.isPresent()) {
                    QuestionVideo question = existingQuestion.get();
                    updateQuestionFromRow(question, row);
                    questionRepository.save(question);
                    updatedCount++;
                }
            }

            return ResponseEntity.ok("Đã thêm " + addedCount + " câu hỏi, cập nhật " + updatedCount + " câu hỏi.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi xử lý file CSV!");
        }
    }

    // 📌 Hàm tạo mới câu hỏi từ CSV
    private QuestionVideo createQuestionFromRow(String[] row) {
        QuestionVideo question = new QuestionVideo();
        question.setQuestionCode(row[1]);

        Lesson lesson = lessonRepository.findById(row[2]).orElse(null);
        // Không còn trường lesson trong QuestionVideo, có thể bỏ hoặc cập nhật theo video nếu cần

        question.setContent(row[3]);
        question.setOptionA(row[4]);
        question.setOptionB(row[5]);
        question.setOptionC(row[6]);
        question.setOptionD(row[7]);
        question.setCorrectOption(row[8].charAt(0));
        question.setExplanation(row[9]);

        return question;
    }

    // 📌 Hàm cập nhật câu hỏi từ CSV
    private void updateQuestionFromRow(QuestionVideo question, String[] row) {
        question.setContent(row[3]);
        question.setOptionA(row[4]);
        question.setOptionB(row[5]);
        question.setOptionC(row[6]);
        question.setOptionD(row[7]);
        question.setCorrectOption(row[8].charAt(0));
        question.setExplanation(row[9]);
    }
}
