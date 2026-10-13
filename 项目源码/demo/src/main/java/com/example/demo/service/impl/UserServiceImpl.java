package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RegisterDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.url-prefix}")
    private String urlPrefix;

    private String encodePassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }
    
    private boolean matchesPassword(String rawPassword, String encodedPassword) {
        return encodePassword(rawPassword).equals(encodedPassword);
    }

    @Override
    @Transactional
    public void register(User user) {
        // 检查用户名是否已存在
        if (lambdaQuery().eq(User::getUsername, user.getUsername()).exists()) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (lambdaQuery().eq(User::getEmail, user.getEmail()).count() > 0) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置默认状态
        user.setStatus(1);
        
        // 保存用户
        save(user);
        logger.info("用户注册成功: {}", user.getUsername());
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        logger.info("尝试登录用户: {}", username);
        
        User user = lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            logger.warn("登录失败: 用户不存在 - {}", username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("登录失败: 密码错误 - {}", username);
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() != 1) {
            logger.warn("登录失败: 账号已被禁用 - {}", username);
            throw new RuntimeException("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId());
        logger.info("用户登录成功: {}", username);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public User getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return getById(userId);
    }

    @Override
    @Transactional
    public void updateUserInfo(User user) {
        Long userId = SecurityUtil.getCurrentUserId();
        user.setId(userId);
        
        // 不允许修改密码
        user.setPassword(null);
        
        // 更新用户信息
        updateById(user);
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = getById(userId);

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        logger.info("用户密码修改成功: {}", user.getUsername());
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        try {
            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 更新用户头像
            Long userId = SecurityUtil.getCurrentUserId();
            User user = getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            String avatarUrl = urlPrefix + "/" + filename;
            user.setAvatar(avatarUrl);
            updateById(user);

            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("上传头像失败", e);
        }
    }
} 