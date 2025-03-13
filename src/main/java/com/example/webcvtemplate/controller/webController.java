package com.example.webcvtemplate.controller;

import com.example.webcvtemplate.entity.Blog;
import com.example.webcvtemplate.entity.Review;
import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.exception.ResourceNotFoundException;
import com.example.webcvtemplate.model.request.UpsertReviewRequest;
import com.example.webcvtemplate.model.responser.ChatRequest;
import com.example.webcvtemplate.model.responser.ChatResponse;
import com.example.webcvtemplate.service.*;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class webController {
    private final HttpSession session;

    private final GeminiService geminiService;

    private final UserService userService;

    private final BlogService blogService;

    private final ReviewService reviewService;

    private final BlogRoleService blogRoleService;





    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Blog> latestBlogs = blogService.getLatestBlogs(14);
        model.addAttribute("latestBlogs", latestBlogs);
        return "web/index";
    }
//    blog
    @GetMapping("/blogs/{blogCode}")
    public String getBlogDetail(@PathVariable String blogCode, Model model, HttpSession session,RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {
        // Tìm blog theo mã
        Blog blog = blogService.findByBlogCode(blogCode)
                .orElseThrow(() -> new ResourceNotFoundException("Blog không tồn tại"));

        // Lấy danh sách review của blog
        List<Review> reviews = reviewService.getReviewsOfMovie(blogCode);

        // Lấy thông tin người dùng hiện tại từ session
        User user = (User) request.getSession().getAttribute("currentUser");

        if (user == null) {
            // Nếu người dùng chưa đăng nhập, lưu đường dẫn hiện tại để chuyển hướng sau khi đăng nhập
            session.setAttribute("redirectAfterLogin", "/blogs/" + blogCode);
        }
        // Đưa dữ liệu vào model
        model.addAttribute("blog", blog);
        model.addAttribute("reviews", reviews);


        return "web/blog/detailBlog"; // Trả về tên template chi tiết blog
    }
        @PostMapping("/reviews/{blogCode}")
        public String createReview(@PathVariable String blogCode, @ModelAttribute UpsertReviewRequest request,
                                   HttpSession session, HttpServletRequest requestContext, RedirectAttributes redirectAttributes) {
            // Lấy thông tin người dùng từ session
            User currentUser = (User) requestContext.getSession().getAttribute("currentUser");

            if (currentUser == null) {
                // Nếu người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập
                session.setAttribute("redirectAfterLogin", "/blogs/" + blogCode);
                return "redirect:/dang-nhap";
            }

            // Gọi service để tạo mới đánh giá
            Review review = reviewService.createReview(request, blogCode, currentUser.getUserCode());

            // Chuyển hướng về trang chi tiết blog với thông báo thành công
            redirectAttributes.addFlashAttribute("message", "Đánh giá của bạn đã được đăng thành công.");
            return "redirect:/blogs/" + blogCode;
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

//    @GetMapping("/cv/{userCode}")
//    public ResponseEntity<Resource> viewCV(@PathVariable String userCode) {
//        try {
//            // Lấy thông tin CV từ cơ sở dữ liệu
//            CVTemplate cvTemplate = cvTemplateService.getCVByUserCode(userCode);
//
//            // Tạo đường dẫn file
//            Path filePath = Paths.get(cvTemplate.getFilePath());
//            if (!Files.exists(filePath)) {
//                throw new IllegalArgumentException("File không tồn tại.");
//            }
//
//            // Tạo tài nguyên Resource để trả về
//            Resource resource = new UrlResource(filePath.toUri());
//
//            // Thiết lập header để hiển thị file PDF
//            return ResponseEntity.ok()
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
//                    .body(resource);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(null);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(null);
//        }
//    }


//    chat ai


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



//    blog hiển thị tại index

//    @GetMapping("/blogIT")
//    public String showITBlogs(Model model) {
//        String itRoleCode = "blr3806"; // Mã mặc định của blog IT
//        List<Blog> blogs = blogService.getBlogsByRole(itRoleCode)
//                .stream()
//                .filter(Blog::getStatus) // Lọc chỉ lấy bài viết có status = true
//                .toList();
//        model.addAttribute("blogs", blogs);
//        return "/web/blogs-by-role-it"; // Trả về trang Thymeleaf
//    }
//
//    @GetMapping("/blogLiving")
//    public String showLiVingBlogs(Model model) {
//        String itRoleCode = "blr3806"; // Mã mặc định của blog IT
//        List<Blog> blogs = blogService.getBlogsByRole(itRoleCode)
//                .stream()
//                .filter(Blog::getStatus) // Lọc chỉ lấy bài viết có status = true
//                .toList();
//        model.addAttribute("blogs", blogs);
//        return "/web/blogs-by-role-it"; // Trả về trang Thymeleaf
//    }


//    nang cao
@GetMapping("/blog/{roleName}")
public String showBlogsByRole(@PathVariable String roleName, Model model) {
    // Lấy mã blogRoleCode từ tên role (do cậu gán sẵn)
    String blogRoleCode = blogRoleService.getRoleCodeByRoleName(roleName);

    if (blogRoleCode == null) {
        return "error/404"; // Nếu không tìm thấy, trả về trang lỗi
    }

    // Lấy danh sách blog theo mã role và lọc status = true
    List<Blog> blogs = blogService.getBlogsByRole(blogRoleCode)
            .stream()
            .filter(Blog::getStatus)
            .toList();

    model.addAttribute("blogs", blogs);
    model.addAttribute("roleName", roleName);
    return "/web/blogs-by-role-it"; // Trả về trang Thymeleaf chung
}


}
