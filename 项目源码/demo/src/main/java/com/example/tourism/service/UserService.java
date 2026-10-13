package com.example.tourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tourism.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
    void register(User user);
    void updateProfile(User user);
} 