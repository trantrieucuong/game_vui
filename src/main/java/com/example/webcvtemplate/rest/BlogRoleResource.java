package com.example.webcvtemplate.rest;

import com.example.webcvtemplate.entity.BlogRole;
import com.example.webcvtemplate.exception.ResourceNotFoundException;
import com.example.webcvtemplate.repository.BlogRoleRepository;
import com.example.webcvtemplate.service.BlogRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog-roles")
@RequiredArgsConstructor
public class BlogRoleResource {
    private final BlogRoleService blogRoleService;
    private final BlogRoleRepository blogRoleRepository;

    // Lấy tất cả blog roles
    @GetMapping
    public ResponseEntity<List<BlogRole>> getAllBlogRoles() {
        List<BlogRole> blogRoles = blogRoleService.getAllBlogRoles();
        return ResponseEntity.ok(blogRoles);
    }

    // Lấy blog role theo mã
    @GetMapping("/{blogRoleCode}")
    public BlogRole getBlogRoleById(String blogRoleCode) {
        return blogRoleRepository.findById(blogRoleCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy BlogRole với mã: " + blogRoleCode));
    }


    // Tạo blog role mới
    @PostMapping
    public ResponseEntity<BlogRole> createBlogRole(@RequestBody BlogRole blogRole) {
        System.out.println("Received BlogRole: " + blogRole); // Kiểm tra dữ liệu đầu vào
        if (blogRole.getRoleName() == null || blogRole.getRoleName().trim().isEmpty()) {
            throw new RuntimeException("Tên Role không được để trống!");
        }
        BlogRole createdBlogRole = blogRoleService.createBlogRole(blogRole);
        return ResponseEntity.ok(createdBlogRole);
    }




    // Xóa blog role
    @DeleteMapping("/delete/{blogRoleCode}")
    public ResponseEntity<String> deleteBlogRole(@PathVariable String blogRoleCode) {
        try {
            boolean isDeleted = blogRoleService.deleteBlogRole(blogRoleCode);
            return ResponseEntity.ok("Xóa thành công!"); // Trả về status 200 OK
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Role!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi trong quá trình xóa!");
        }
    }


}
