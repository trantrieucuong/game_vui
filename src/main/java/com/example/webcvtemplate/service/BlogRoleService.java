package com.example.webcvtemplate.service;

import com.example.webcvtemplate.entity.BlogRole;
import com.example.webcvtemplate.exception.ResourceNotFoundException;
import com.example.webcvtemplate.repository.BlogRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BlogRoleService {
    private final BlogRoleRepository blogRoleRepository;
    private final Random random = new Random();

    // Lấy danh sách tất cả các BlogRole
    public List<BlogRole> getAllBlogRoles() {
        return blogRoleRepository.findAll();
    }

    // Lấy một BlogRole theo mã
    public BlogRole getBlogRoleByCode(String blogRoleCode) {
        return blogRoleRepository.findById(blogRoleCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy BlogRole với mã: " + blogRoleCode));
    }
// thêm roleblog
    private String generateBlogRoleCode() {
        int number;
        String code;
        do {
            // Tạo số ngẫu nhiên từ 0000 đến 9999
            number = random.nextInt(10000);
            code = String.format("blr%04d", number);
        } while (blogRoleRepository.existsById(code)); // Kiểm tra xem mã đã tồn tại chưa

        return code;
    }

    @Transactional
    public BlogRole createBlogRole(BlogRole blogRole) {
        if (blogRole.getBlogRoleCode() == null || blogRole.getBlogRoleCode().isEmpty()) {
            blogRole.setBlogRoleCode(generateBlogRoleCode());
        }
        if (blogRoleRepository.existsById(blogRole.getBlogRoleCode())) {
            throw new RuntimeException("Mã BlogRole đã tồn tại!");
        }
        return blogRoleRepository.save(blogRole);
    }

    // Cập nhật BlogRole
    public BlogRole updateBlogRole(String blogRoleCode, BlogRole updatedBlogRole) {
        return blogRoleRepository.findById(blogRoleCode).map(existingRole -> {
            existingRole.setRoleName(updatedBlogRole.getRoleName());
            return blogRoleRepository.save(existingRole);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy mã role: " + blogRoleCode));
    }

    // Xóa BlogRole theo mã
    public boolean deleteBlogRole(String blogRoleCode) {
        // Kiểm tra xem blog role có tồn tại không
        BlogRole blogRole = blogRoleRepository.findById(blogRoleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy vai trò blog với mã = " + blogRoleCode));
        // Xóa blog role khỏi database
        System.out.println(blogRole);
        if (!blogRole.getBlogs().isEmpty()) {
            throw new RuntimeException("Không thể xóa role vì đang được Blog sử dụng");
        }
        blogRoleRepository.delete(blogRole);
        return true;
    }

    public String getRoleCodeByRoleName(String roleName) {
        BlogRole role = blogRoleRepository.findByRoleName(roleName).orElse(null);
        return role != null ? role.getBlogRoleCode() : null;
    }



}
