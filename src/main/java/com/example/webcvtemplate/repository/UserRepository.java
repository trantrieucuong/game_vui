package com.example.webcvtemplate.repository;



import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.model.enums.UserRole;
import jakarta.transaction.Transactional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByRole(UserRole userRole);

    List<User> findByRoleTrue();

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByEmailAndRole(String email, UserRole role);

    Optional<User> findByUserCode(String userCode);
    void deleteByUserCode(String userCode);
    boolean existsByUserCode(String userCode);
    Optional<User> findUserByUserCode(String userCode);
    // Tìm mã OTP theo email
//    User getUserByCode(String userCode);
    @Query("SELECT u FROM User u WHERE u.userCode = :userCode")
    User getUserByCode(@Param("userCode") String userCode);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.otp = :otp WHERE u.email = :email")
    void updateOtp(@Param("email") String email, @Param("otp") String otp);

    User findByName(String name);




}