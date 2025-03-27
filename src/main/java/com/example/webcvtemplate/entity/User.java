package com.example.webcvtemplate.entity;

import com.example.webcvtemplate.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_code", length = 10, nullable = false)
    private String userCode = "us" + String.format("%05d", (int)(Math.random() * 100000));
    String name;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String password;

    String avatar;

    String nameCompaly;

    String sdt;

    String address;

    @Enumerated(EnumType.STRING)
    UserRole role;

    @Column(nullable = true)
    private String otp;

    @Column(nullable = true)
    private LocalDateTime otpExpiry;

    @Column(nullable = false)
    private Boolean isVerified = Boolean.FALSE; // Đặt giá trị mặc định là false



    @Override
    public String toString() {
        return "User{" +
                "userCode='" + userCode + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sdt='" + sdt + '\'' +
                ", address='" + address + '\'' +
                ", role=" + role +
                ", otp='" + otp + '\'' +
                ", otpExpiry=" + otpExpiry +
                ", isVerified=" + isVerified +
                '}';
    }
}
