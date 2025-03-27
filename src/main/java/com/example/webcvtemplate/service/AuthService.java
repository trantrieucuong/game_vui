package com.example.webcvtemplate.service;

import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.exception.ResourceNotFoundException;
import com.example.webcvtemplate.model.enums.UserRole;
import com.example.webcvtemplate.model.request.LoginRequest;
import com.example.webcvtemplate.model.request.RegisterRequest;
import com.example.webcvtemplate.repository.UserRepository;
import com.example.webcvtemplate.service.email.EmailService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final HttpSession session;
    private final EmailService emailService;


    public User login(LoginRequest request) throws BadRequestException {
        // Tìm người dùng theo email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email hoặc mật khẩu không đúng"));

        // Kiểm tra mật khẩu
        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Email hoặc mật khẩu không đúng");
        }

        // Kiểm tra trạng thái xác thực
        if (!user.getIsVerified()) {
            throw new BadRequestException("Tài khoản chưa được xác thực. Vui lòng kiểm tra email để xác thực tài khoản.");
        }

        // Lưu thông tin người dùng vào session (chỉ khi tài khoản đã xác thực)
        session.setAttribute("currentUser", user);

        return user;
    }



    private String generateUserCode() {
        // Tạo số ngẫu nhiên từ 0 đến 9999
        int number = new Random().nextInt(10000);
        // Định dạng số với 4 chữ số và tiền tố "us"
        return String.format("us%04d", number);
    }





    public void logout() {
        // Xóa thông tin user trong session
        // session.removeAttribute("currentUser");

        // set current user to null
        session.setAttribute("currentUser", null);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public void register(RegisterRequest request) throws BadRequestException {
        // Kiểm tra xem email đã tồn tại chưa
        if (userRepository.existsByEmailAndRole(request.getEmail(), UserRole.USER)) {
            throw new BadRequestException("Email đã tồn tại");
        }

        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu không trùng khớp");
        }

        // Mã hóa mật khẩu
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        // Tạo User (chưa tạo tài khoản chính thức, chỉ tạo OTP)
        User user = User.builder()
                .userCode(generateUserCode())
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .address(request.getAddress())
                .sdt(request.getSdt())
                .role(UserRole.USER)
                .otp(generateOtp())  // Tạo OTP
                .isVerified(false)
                .otpExpiry(LocalDateTime.now().plusMinutes(5))  // Thời gian hết hạn OTP (15 phút)
                .build();

        // Lưu User tạm thời
        userRepository.save(user);

        // Gửi OTP qua email
        String subject = "Mã xác nhận đăng ký";
        String body = "Mã OTP của bạn là: " + user.getOtp();
        emailService.sendEmail(user.getEmail(), subject, body);  // Gửi mã OTP qua email
    }

    public void registerAdmin(RegisterRequest request) throws BadRequestException {
        // Kiểm tra xem email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại");
        }

        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu không trùng khớp");
        }

        // Mã hóa mật khẩu
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        // Tạo User (chưa tạo tài khoản chính thức, chỉ tạo OTP)
        User user = User.builder()
                .userCode(generateUserCode())
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .address(request.getAddress())
                .sdt(request.getSdt())
                .role(UserRole.ADMIN)
                .otp(generateOtp())  // Tạo OTP
                .isVerified(false)
                .otpExpiry(LocalDateTime.now().plusMinutes(5))  // Thời gian hết hạn OTP (15 phút)
                .build();

        // Lưu User tạm thời
        userRepository.save(user);

        // Gửi OTP qua email
        String subject = "Mã xác nhận đăng ký";
        String body = "Mã OTP của bạn là: " + user.getOtp();
        emailService.sendEmail(user.getEmail(), subject, body);  // Gửi mã OTP qua email
    }
    public String generateOtp() {
        // Tạo mã OTP ngẫu nhiên, ví dụ mã 6 chữ số
        int otp = new Random().nextInt(900000) + 100000;  // Tạo số từ 100000 đến 999999
        return String.valueOf(otp);
    }
    public void verifyOtp(String email, String otp) throws BadRequestException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));

        if (!user.getOtp().equals(otp)) {
            throw new BadRequestException("Mã OTP không chính xác");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn");
        }

        user.setOtp(null); // Remove OTP after verification
        user.setOtpExpiry(null); // Clear expiry time
        user.setIsVerified(true); // Mark the account as verified
        userRepository.save(user);
    }

    @Transactional
    public String resendOtp(String email) throws BadRequestException {
        // Kiểm tra email có tồn tại trong hệ thống không
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Email không tồn tại trong hệ thống"));

        // Tạo mã OTP mới
        String newOtp = generateOtp();

        // Cập nhật mã OTP mới cho người dùng
        userRepository.updateOtp(email, newOtp);

        // Gửi mã OTP mới qua email
        emailService.sendEmail(email, "Gửi lại mã OTP", "Mã OTP mới của bạn là: " + newOtp);

        return newOtp;
    }

    @Transactional
    public void deleteExpiredUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));

        // Kiểm tra nếu OTP đã hết hạn mà không được xác thực
        if (user.getOtpExpiry().isBefore(LocalDateTime.now()) && !user.getIsVerified()) {
            userRepository.delete(user); // Xóa tài khoản tạm thời
        }
    }









}
