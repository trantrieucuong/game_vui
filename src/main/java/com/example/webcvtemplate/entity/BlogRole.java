package com.example.webcvtemplate.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "blog_role")
public class BlogRole {// có nhiều role khác nhau, có thể tạo thêm role
    @Id
    @Column(name = "blog_role_code", length = 10, nullable = false)
    String blogRoleCode;
    String roleName;
    @ManyToMany(mappedBy = "blogRoles")
     // Kiểm soát vòng lặp JSON
    private Set<Blog> blogs = new HashSet<>();

}
