package com.example.webcvtemplate.controller;

import com.example.webcvtemplate.entity.Blog;
import com.example.webcvtemplate.entity.BlogRole;
import com.example.webcvtemplate.service.BlogRoleService;
import com.example.webcvtemplate.service.BlogService;
import lombok.RequiredArgsConstructor;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/blogs")
public class BlogController {
    private final BlogService blogService;
    private final BlogRoleService blogRoleService;

    // Danh sách tất cả bài viết
    @GetMapping
    public String getHomePage(Model model) {
        // Lấy tất cả bài viết sắp xếp theo createdAt giảm dần
        List<Blog> blogList = blogService.getAllBlogs();
        model.addAttribute("blogList", blogList);
        return "admin/blog/index";
    }

    // Danh sách bài viết của tôi
    @GetMapping("/own-blogs")
    public String getOwnPage(Model model) {
        // Lấy tất cả của user đang đăng nhập, sắp xếp theo createdAt giảm dần
        // Lấy user đang đăng nhập lấy trong session với key là "currentUser"
        // Lấy bài viết theo userId
        List<Blog> blogList = blogService.getAllBlogOfCurrentUser();
        model.addAttribute("blogList", blogList);
        return "admin/blog/own-blog";
    }

    // Tạo bài viết
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        List<BlogRole> blogRoles = blogRoleService.getAllBlogRoles(); // Giả sử có service lấy danh sách roles
        model.addAttribute("blogRoles", blogRoles);
        return "admin/blog/create";
    }

    @GetMapping("/{blogCode}/detail")
    public String getDetailPage(@PathVariable String blogCode, Model model) {
        // Lấy bài viết theo blogCode
        Blog blog = blogService.getBlogById(blogCode);

        // Lấy danh sách blogRoles
        List<BlogRole> blogRoles = blogRoleService.getAllBlogRoles();

        // Kiểm tra xem blog có tồn tại hay không
        if (blog != null) {
            model.addAttribute("blog", blog);
            model.addAttribute("blogRoles", blogRoles); // Thêm danh sách blogRoles
            model.addAttribute("blogCode", blogCode); // Thêm blogCode vào model
            return "admin/blog/detail"; // Trả về template Thymeleaf
        } else {
            // Nếu không tìm thấy blog, có thể chuyển hướng hoặc hiển thị thông báo lỗi
            return "admin/error"; // Hoặc chuyển hướng về trang lỗi
        }
    }

}
