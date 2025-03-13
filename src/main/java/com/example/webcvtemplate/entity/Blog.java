package com.example.webcvtemplate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "blogs")
public class Blog {
    @Id
    @Column(name = "blog_code", length = 10, nullable = false)
    String blogCode;

    String title;
    String slug;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(columnDefinition = "TEXT")
    String content;

    String thumbnail;
    Boolean status;

    Date createdAt;
    Date updatedAt;
    Date publishedAt;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonIgnore // Ngăn vòng lặp vô hạn khi serialize JSON
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "blog_roles",
            joinColumns = @JoinColumn(name = "blog_code"),
            inverseJoinColumns = @JoinColumn(name = "blog_role_code")
    )
     // Kiểm soát vòng lặp JSON
    private Set<BlogRole> blogRoles = new HashSet<>();




    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        updatedAt = new Date();
        if(status) {
            publishedAt = new Date();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
        if(status) {
            publishedAt = new Date();
        } else {
            publishedAt = null;
        }

    }

    @Override
    public String toString() {
        return "Blog{" +
                "blogCode='" + blogCode + '\'' +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", description='" + description + '\'' +
                ", content='" + content + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", publishedAt=" + publishedAt +
                ", user=" + user +
                '}';
    }
}
