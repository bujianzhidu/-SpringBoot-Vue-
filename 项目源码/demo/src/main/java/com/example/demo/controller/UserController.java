package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.LoginDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "用户管理", description = "用户注册、登录、信息管理等接口")
@RestController
@RequestMapping({"/api/user", "/api/users"})
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    
    @Operation(
        summary = "用户注册",
        description = "注册新用户，需要提供用户名、密码、邮箱等信息",
        responses = {
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "注册失败，可能是用户名或邮箱已存在")
        }
    )
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        logger.info("Received register request for user: {}", user.getUsername());
        userService.register(user);
        return Result.success();
    }
    
    @Operation(
        summary = "用户登录",
        description = "用户登录接口，需要提供用户名和密码",
        responses = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "登录失败，用户名或密码错误")
        }
    )
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        logger.info("Received login request for user: {}", loginDTO.getUsername());
        Map<String, Object> result = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return Result.success(result);
    }
    
    @Operation(
        summary = "获取当前用户信息",
        description = "获取当前登录用户的详细信息",
        responses = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录")
        }
    )
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        logger.info("Received get user info request");
        return Result.success(userService.getCurrentUser());
    }
    
    @Operation(
        summary = "更新用户信息",
        description = "更新指定用户的信息",
        responses = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
        }
    )
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody User user) {
        logger.info("Received update user info request for user: {}", user.getUsername());
        userService.updateUserInfo(user);
        return Result.success();
    }
    
    @Operation(
        summary = "修改密码",
        description = "修改当前用户的登录密码",
        responses = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "400", description = "原密码错误"),
            @ApiResponse(responseCode = "401", description = "未登录")
        }
    )
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody PasswordChangeRequest request) {
        logger.info("Received change password request");
        userService.changePassword(request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        logger.info("Received upload avatar request");
        String avatarUrl = userService.uploadAvatar(file);
        return Result.success(avatarUrl);
    }

    @lombok.Data
    public static class PasswordChangeRequest {
        private String oldPassword;
        private String newPassword;
    }
} 