package com.example.webcvtemplate.service;

import com.example.webcvtemplate.entity.User;
import com.example.webcvtemplate.exception.ResourceNotFoundException;
import com.example.webcvtemplate.model.enums.UserRole;
import com.example.webcvtemplate.model.request.UserRequest;
import com.example.webcvtemplate.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileService fileService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(UserRequest userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }
        User user = new User();

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
//        user.setAvatar(userDTO.getAvatar());
        user.setSdt(userDTO.getSdt());
        user.setProvince(userDTO.getProvince());
        user.setDistrict(userDTO.getDistrict());
        user.setTown(userDTO.getTown());
        user.setAddress(userDTO.getAddress());
        if (userDTO.getRole() == null) {
            user.setRole(UserRole.USER);
        }
        return userRepository.save(user);
    }
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    @Transactional
    public void deleteUserByCode(String userCode) {
        try {
            if (userRepository.existsByUserCode(userCode)) {
                userRepository.deleteByUserCode(userCode);
                System.out.println("User đã được xóa: " + userCode);
            } else {
                System.out.println("Không tìm thấy user với mã: " + userCode);
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi xóa user: " + e.getMessage());
            throw new RuntimeException("Lỗi khi xóa user");
        }
    }
    public Optional<User> getUserDetailByCode(String userCode) {
        return userRepository.findUserByUserCode(userCode);
    }

    public Optional<User> findByUserCode(String userCode ) {
        return userRepository.findUserByUserCode(userCode);
    }
    public User updateUserEmployer(String userCode, String nameCompaly, UserRole role) {
        // Tìm người dùng theo userCode
        Optional<User> optionalUser = userRepository.findById(userCode);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Cập nhật thông tin
            if (nameCompaly != null) {
                user.setNameCompaly(nameCompaly);
            }
            if (role != null) {
                user.setRole(role);
            }

            // Lưu lại vào cơ sở dữ liệu
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Người dùng không tồn tại với mã: " + userCode);
        }
    }
    public User updateUser(String userCode, User userCu) {

        if (userCu == null) {
            throw new IllegalArgumentException("Thông tin người dùng không hợp lệ.");
        }
        Optional<User> userOpt = userRepository.findUserByUserCode(userCode);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy người dùng với mã " + userCode);
        }
        User user = userOpt.get();

        user.setName(userCu.getName());
        user.setEmail(userCu.getEmail());
        user.setSdt(userCu.getSdt());
        user.setAddress(userCu.getAddress());

        User updatedUser = userRepository.save(user);
        if (updatedUser == null) {
            throw new RuntimeException("Không thể cập nhật thông tin người dùng.");
        }
        return updatedUser;
    }




    private static final String UPLOAD_DIR = "src/main/resources/static/img/";

    public String uploadThumbnail(String userCode, MultipartFile file) {
        try {
            // Tạo tên file và đường dẫn
            String fileName = userCode + "-" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);

            // Kiểm tra và tạo thư mục nếu chưa tồn tại
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Lưu file vào hệ thống
            Files.write(path, file.getBytes());

            // Cập nhật đường dẫn vào cơ sở dữ liệu
            String filePath = "/img/" + fileName;
            Optional<User> optionalUser = userRepository.findByUserCode(userCode);
            if (optionalUser.isPresent()) {
                User user = userRepository.findByUserCode(userCode)
                        .orElseThrow(() -> new RuntimeException("User not found with code: " + userCode));
                user.setAvatar(filePath); // Cập nhật avatar
                userRepository.save(user); // Lưu lại thông tin
                // Lưu lại thay đổi
            } else {
                throw new RuntimeException("User not found with code: " + userCode);
            }

            return filePath; // Trả về đường dẫn ảnh
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Nếu có lỗi, trả về null
        }
    }


}
