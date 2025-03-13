package com.example.webcvtemplate.config.websocket;




import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

@Controller
public class ChatController {

    private static final List<String> BANNED_WORDS = Arrays.asList(
            "đm", "dm", "địt", "đjt", "clgt", "cl", "vl", "vkl","vãi",
            "vcl", "cặc", "lồn", "đụ", "đéo", "dcm", "cc", "chó",
            "má", "mẹ", "bố", "mả", "phò", "ngu", "khốn", "nứng",
            "chịch", "bậy", "đú", "fuck", "shit", "bitch", "asshole"
    );
    private static final List<String> BANNED_PHRASES = Arrays.asList(
            "2tcamera không uy tín",
            "đi mua quán khác uy tín hơn"
    );

    private boolean containsBannedWordsOrPhrases(String message) {
        String sanitizedMessage = message.toLowerCase().replaceAll("\\s+", " ").trim();
        return BANNED_WORDS.stream().anyMatch(sanitizedMessage::contains) ||
                BANNED_PHRASES.stream().anyMatch(sanitizedMessage::contains);
    }


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // Kiểm tra ngôn từ
        if (containsBannedWordsOrPhrases(chatMessage.getContent())) {
            chatMessage.setContent("Cái mồm của bạn đang mất kiểm soát. Vui lòng cư sử văn minh.");
            chatMessage.setType(MessageType.LEAVE); // Hoặc tạo một loại thông báo mới.
        }
        return chatMessage;
    }

//    private boolean containsBannedWords(String message) {
//        return BANNED_WORDS.stream().anyMatch(message.toLowerCase()::contains);
//    }
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        // Giới hạn người dùng chỉ có "user" và "admin"
        if (!"user".equals(chatMessage.getSender()) && !"admin".equals(chatMessage.getSender())) {
            chatMessage.setContent("Only user and admin are allowed!");
            chatMessage.setType(MessageType.LEAVE);
        }
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}