package com.example.webcvtemplate.service;


import com.example.webcvtemplate.model.responser.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    @Value("${google.api.key}")
    private String apiKey;

    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent";

    public ChatResponse getAnswer(String question) {
        RestTemplate restTemplate = new RestTemplate();

        // Tạo body JSON
        String body = """
        {
          "contents": [
            {
              "parts": [
                { "text": "%s" }
              ]
            }
          ]
        }
        """.formatted(question);

        // Cấu hình Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo HttpEntity
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            // Gửi POST request đến API
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL + "?key=" + apiKey,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // Xử lý JSON trả về
            return processResponse(response.getBody());
        } catch (Exception e) {
            return new ChatResponse("Lỗi khi kết nối với Gemini API: " + e.getMessage());
        }
    }

    private ChatResponse processResponse(String responseBody) {
        try {
            // In ra responseBody để kiểm tra cấu trúc JSON
            System.out.println("Response Body: " + responseBody);

            // Dùng ObjectMapper để phân tích JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // Trích xuất từ candidates -> content -> parts -> text
            JsonNode candidatesNode = rootNode.path("candidates");
            if (candidatesNode.isArray() && candidatesNode.size() > 0) {
                JsonNode contentNode = candidatesNode.get(0).path("content");
                JsonNode partsNode = contentNode.path("parts");
                if (partsNode.isArray() && partsNode.size() > 0) {
                    String answer = partsNode.get(0).path("text").asText();
                    return new ChatResponse(answer); // Trả về câu trả lời
                }
            }

            // Nếu không tìm thấy câu trả lời, trả về thông báo lỗi
            return new ChatResponse("Không tìm thấy câu trả lời trong phản hồi.");
        } catch (Exception e) {
            // Xử lý ngoại lệ và trả về thông báo lỗi
            return new ChatResponse("Lỗi khi phân tích JSON: " + e.getMessage());
        }
    }

}