package com.example.webcvtemplate.config;

import com.example.webcvtemplate.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Lấy thông tin người dùng từ trong session với key currentUser
        User user = (User) request.getSession().getAttribute("currentUser");
        HttpSession session = request.getSession(false);

// Kiểm tra trạng thái đăng nhập
        if (session == null || session.getAttribute("loggedInUser") == null) {
            // Lưu trữ URL hiện tại vào session
            String redirectUrl = request.getRequestURI();
            if (request.getQueryString() != null) {
                redirectUrl += "?" + request.getQueryString();
            }
            session.setAttribute("redirectAfterLogin", redirectUrl);

            // Chuyển hướng đến trang đăng nhập
            response.sendRedirect("/dang-nhap");
            return false;
        }


        return true;
        // Nếu currentUser không tồn tại hoặc = null thì báo lỗi 401 (unauthorized)

    }

}
