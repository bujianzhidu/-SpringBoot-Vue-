package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService extends IService<User> {
    void register(User user);
    Map<String, Object> login(String username, String password);
    User getCurrentUser();
    void updateUserInfo(User user);
    void changePassword(String oldPassword, String newPassword);
    String uploadAvatar(MultipartFile file);
} 