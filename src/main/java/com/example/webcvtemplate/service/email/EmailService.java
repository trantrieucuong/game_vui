package com.example.webcvtemplate.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Tạo mã OTP 6 chữ số
        return String.valueOf(otp);
    }

    public void sendOtpEmail(String email, String otp) {
        String subject = "Mã xác nhận đăng ký tài khoản";
        String body = "Mã OTP của bạn là: " + otp + "\nVui lòng nhập mã OTP này để hoàn tất đăng ký.";
        sendEmail(email, subject, body);
    }







}

