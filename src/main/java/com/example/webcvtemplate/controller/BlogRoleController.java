package com.example.webcvtemplate.controller;

import com.example.webcvtemplate.entity.BlogRole;
import com.example.webcvtemplate.service.BlogRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/blog-roles")
public class BlogRoleController {
    private final BlogRoleService blogRoleService;

    // Danh sách tất cả vai trò
    @GetMapping
    public String getAllBlogRoles(Model model) {
        List<BlogRole> blogRoles = blogRoleService.getAllBlogRoles();
        model.addAttribute("blogRoles", blogRoles);
        return "admin/blog-role/index";
    }

    // Form tạo vai trò mới
    @GetMapping("/create")
    public String getCreatePage(Model model) {
        model.addAttribute("blogRole", new BlogRole());
        return "admin/blog-role/create";
    }

    // Xử lý tạo vai trò
    @PostMapping("/create")
    public String createBlogRole(@ModelAttribute BlogRole blogRole) {
        blogRoleService.createBlogRole(blogRole);
        return "redirect:/admin/blog-roles";
    }

}
