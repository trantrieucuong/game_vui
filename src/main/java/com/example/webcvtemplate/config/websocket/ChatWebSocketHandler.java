package com.example.webcvtemplate.config.websocket;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;

import org.springframework.stereotype.Component;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Khi kết nối WebSocket được thiết lập, bạn có thể lưu trữ session nếu cần
        System.out.println("User connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Xử lý tin nhắn đến từ client
        String userMessage = message.getPayload();

        // Phản hồi lại tin nhắn từ bot
        String botResponse = "Chào " + userMessage; // Giả sử userMessage là tên người dùng

        // Gửi tin nhắn phản hồi cho client
        session.sendMessage(new TextMessage(botResponse));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        System.out.println("User disconnected: " + session.getId());
    }
}
