package com.example.demo.controller;

import com.example.demo.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@Tag(name = "文件管理", description = "文件上传相关的接口")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @PostMapping("/avatar/{userId}")
    @Operation(summary = "上传用户头像")
    public String uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        return fileService.uploadAvatar(file, userId);
    }
    
    @PostMapping("/transaction/{transactionId}")
    @Operation(summary = "上传交易凭证图片")
    public String uploadTransactionImage(@PathVariable Long transactionId, @RequestParam("file") MultipartFile file) {
        return fileService.uploadTransactionImage(file, transactionId);
    }
    
    @DeleteMapping
    @Operation(summary = "删除文件")
    public void deleteFile(@RequestParam String fileUrl) {
        fileService.deleteFile(fileUrl);
    }
} 