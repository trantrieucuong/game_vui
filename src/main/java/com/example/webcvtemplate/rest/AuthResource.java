package com.example.webcvtemplate.rest;

import com.example.webcvtemplate.entity.Blog;
import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.exception.ResourceNotFoundException;
import com.example.webcvtemplate.model.request.LoginRequest;
import com.example.webcvtemplate.model.request.RegisterRequest;
import com.example.webcvtemplate.repository.UserRepository;
import com.example.webcvtemplate.service.AuthService;
import com.example.webcvtemplate.service.FileService;
import com.example.webcvtemplate.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthResource {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws BadRequestException {

        // Kiểm tra xem email và password có hợp lệ không
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email và mật khẩu không được để trống"));
        }

        User user = authService.login(request); // Call login method

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Tài khoản không tồn tại"
            ));
        }

        if (!user.getIsVerified()) { // Kiểm tra tài khoản đã được xác thực chưa
            String redirectUrl = "/xac-thuc-otp?email=" + user.getEmail();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "message", "Tài khoản chưa được xác thực",
                    "redirectUrl", redirectUrl
            ));
        }


        return ResponseEntity.ok(Map.of(
                "message", "Đăng nhập thành công",
                "user", user
        ));
    }






    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) throws BadRequestException {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dang_ky")
    public ResponseEntity<?> dangKy(@RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.ok("Đăng ký thành công");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/xac-thuc-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) throws BadRequestException {

        String email = request.get("email");
        String otp = request.get("otp");


        if (email == null || otp == null) {
            throw new BadRequestException("Email và mã OTP là bắt buộc");
        }

        try {
            authService.verifyOtp(email, otp);
            return ResponseEntity.ok("Tài khoản đã được xác thực thành công!");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/gui-lai-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> request) throws BadRequestException {
        String email = request.get("email");

        // Kiểm tra email có tồn tại trong request
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email là bắt buộc để gửi lại mã OTP");
        }

        try {
            // Tìm kiếm người dùng theo email
            User user = authService.findByEmail(email)
                    .orElseThrow(() -> new BadRequestException("Email không tồn tại trong hệ thống"));

            // Kiểm tra tài khoản đã xác thực chưa
            if (Boolean.TRUE.equals(user.getIsVerified())) {
                return ResponseEntity.badRequest().body("Tài khoản đã được xác thực. Không cần gửi lại mã OTP.");
            }

            // Gửi lại OTP
            String otp = authService.resendOtp(email);

            return ResponseEntity.ok("Mã OTP đã được gửi lại thành công!");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi gửi lại mã OTP. Vui lòng thử lại sau.");
        }
    }

    @PostMapping("/delete-expired-user")
    public ResponseEntity<?> deleteExpiredUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.deleteExpiredUser(email);
        return ResponseEntity.ok(Map.of("message", "Tài khoản tạm thời đã bị xóa"));
    }

    @PostMapping("/{userCode}/upload-thumbnail")
    public ResponseEntity<String> uploadThumbnail(@RequestParam("file") MultipartFile file, @PathVariable String userCode) {
        try {
            String filePath = userService.uploadThumbnail(userCode, file);
            if (filePath == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"message\":\"Không thể tải ảnh lên\"}");
            }
            return ResponseEntity.ok("{\"filePath\":\"" + filePath + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"Lỗi khi xử lý yêu cầu\"}");
        }
    }
    // API cập nhật thông tin người dùng
    @PutMapping("/{userCode}")
    public ResponseEntity<?> updateUser(@PathVariable String userCode, @RequestBody User userCu) {
        try {
            // Gọi service để cập nhật thông tin người dùng
            User updatedUser = userService.updateUser(userCode, userCu);
            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật thành công",
                    "user", updatedUser
            )); // Trả về thông báo và thông tin người dùng đã cập nhật
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "Không tìm thấy người dùng",
                    "error", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Dữ liệu không hợp lệ",
                    "error", e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Đã xảy ra lỗi khi cập nhật người dùng",
                    "error", e.getMessage()
            ));
        }
    }


}
