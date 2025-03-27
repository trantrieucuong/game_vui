package com.example.webcvtemplate.controller;

import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.exception.ResourceNotFoundException;
import com.example.webcvtemplate.model.request.UpsertReviewRequest;
import com.example.webcvtemplate.model.responser.ChatRequest;
import com.example.webcvtemplate.model.responser.ChatResponse;
import com.example.webcvtemplate.service.*;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class webController {
    private final HttpSession session;

    private final GeminiService geminiService;

    private final UserService userService;






    @GetMapping("/")
    public String getHomePage(Model model) {
        return "web/index";
    }
    @GetMapping("/trangchu")
    public String getHomePageGame(Model model) {
        return "web/index";
    }

    @GetMapping("/dang-ky")
    public String getDangKyPage() {
        User user = (User) session.getAttribute("currentUser"); // Lấy thông tin người dùng trong session
        if (user != null) { // Nếu đăng nhập thì chuyển hướng về trang chủ
            return "redirect:/";
        }
        return "web/dang-ky"; // Nếu chưa đăng nhập thì hiển thị trang đăng ký
    }
    @GetMapping("/dang-nhap")
    public String getDangNhapPage() {
        User user = (User) session.getAttribute("currentUser"); // Lấy thông tin người dùng trong session
        if (user != null) { // Nếu đăng nhập thì chuyển hướng về trang chủ
            return "redirect:/";
        }
        return "web/dang-nhap"; // Nếu chưa đăng nhập thì hiển thị trang đăng nhập
    }




    @GetMapping("/editprofile/{user_code}")
    public String editProfile(@PathVariable String user_code,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        User user = (User) request.getSession().getAttribute("currentUser");

        // Kiểm tra đăng nhập
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để sử dụng tính năng này");
            return "redirect:/dang-nhap";
        }

        // Kiểm tra user_code
        if (!user.getUserCode().equals(user_code)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập vào trang này");
            return "redirect:/";
        }

        Optional<User> Users = userService.findByUserCode(user_code);

        if (Users.isPresent()) {

            model.addAttribute("user", Users.get());

        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng");
            return "redirect:/";
        }

        return "web/profile";
    }


    private List<Message> messages = new ArrayList<>();

    @GetMapping("/chat")
    public String chatai(Model model) {
        model.addAttribute("messages", messages);
        return "web/chat";  // trả về trang chat để render
    }

    // API để xử lý câu hỏi và trả lời
    @PostMapping("/ask")
    @ResponseBody  // Đảm bảo trả về JSON thay vì template
    public ChatResponse ask(@RequestBody ChatRequest chatRequest) {
        messages.add(new Message("user", chatRequest.getQuestion()));

        // Gửi câu hỏi đến Gemini API và nhận câu trả lời
        ChatResponse response = geminiService.getAnswer(chatRequest.getQuestion());

        // Thêm câu trả lời của bot vào danh sách tin nhắn
        messages.add(new Message("bot", response.getAnswer()));

        return response; // Trả về JSON cho frontend
    }
    public static class Message {
        private String sender;
        private String text;

        public Message(String sender, String text) {
            this.sender = sender;
            this.text = text;
        }

        public String getSender() {
            return sender;
        }

        public String getText() {
            return text;
        }
    }
    @GetMapping("/exit")
    public String exit() {
        // Xóa danh sách tin nhắn
        messages.clear();
        return "redirect:/"; // Chuyển hướng lại trang chat sau khi thoát
    }

}
