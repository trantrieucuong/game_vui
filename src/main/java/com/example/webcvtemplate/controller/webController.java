package com.example.webcvtemplate.controller;

import com.example.webcvtemplate.entity.Lesson;
import com.example.webcvtemplate.entity.QuestionVideo;
import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.entity.Video;
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
    private final VideoService videoService;
    private final QuestionService questionVideoService;
    private final LessonService lessonService;






    @GetMapping("/")
    public String getHomePage(Model model) {
        return "web/index";
    }
    @GetMapping("/starst")
    public String getHomePage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            return "web/starst";  // Trả về trang chủ nếu đã đăng nhập
        }
        return "redirect:/dang-nhap";  // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
    }
    @GetMapping("/thele")
    public String getHomePageThele(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            // Lấy danh sách bài học từ database (Giả sử bạn có một service để lấy lesson)
            List<Lesson> lessons = lessonService.getAllLessons();  // Lấy tất cả bài học

            if (!lessons.isEmpty()) {
                // Lấy lessonCode của bài học đầu tiên (ví dụ 'history101')
                String lessonCode = lessons.get(0).getLessonCode();  // Giả sử getLessonCode() trả về mã bài học
                model.addAttribute("lessonCode", lessonCode);  // Gán lessonCode vào model
            }

            // Trả về trang thể lệ với lessonCode đã gán
            return "web/thele";
        }

        // Chuyển hướng đến trang đăng nhập nếu người dùng chưa đăng nhập
        return "redirect:/dang-nhap";
    }

    @GetMapping("/game")
    public String getAllLessons(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        List<Lesson> lessons = lessonService.getAllLessons();
        model.addAttribute("lessons", lessons);
        model.addAttribute("user", user); // nếu cần dùng trong view

        return "web/game";
    }

    @GetMapping("/game/lesson/{lessonCode}")
    public String viewGameLesson(@PathVariable String lessonCode, Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/dang-nhap";
        }

        Lesson lesson = lessonService.getLessonByLessonCode(lessonCode);
        List<Video> videos = videoService.getVideosByLessonCode(lessonCode);
        model.addAttribute("lesson", lesson);
        model.addAttribute("videos", videos);
        model.addAttribute("user", user); // nếu muốn hiển thị tên người dùng, v.v.

        return "web/lesson-game";
    }



//    @GetMapping("/game/{lessonCode}")
//    public String startGame(@PathVariable("lessonCode") String lessonCode, Model model) {
//        // Lấy danh sách video theo lessonCode
//        List<Video> videos = videoService.getVideosByLessonCode(lessonCode);
//
//        // Lấy video đầu tiên
//        Video firstVideo = videos.get(0);
//
//        // Lấy câu hỏi của video đầu tiên
//        List<QuestionVideo> questions = questionVideoService.getQuestionsByVideoCode(firstVideo.getVideoCode());
//
//        // Thêm dữ liệu vào model
//        model.addAttribute("video", firstVideo);
//        model.addAttribute("questions", questions);
//
//        // Trả về trang game
//        return "web/game";
//    }


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
            return "redirect:/starst";
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
