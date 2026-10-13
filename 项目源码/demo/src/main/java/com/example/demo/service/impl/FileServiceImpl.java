package com.example.demo.service.impl;

import com.example.demo.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.access.path}")
    private String accessPath;
    
    @Override
    public String uploadAvatar(MultipartFile file, Long userId) {
        return uploadFile(file, "avatar", userId.toString());
    }
    
    @Override
    public String uploadTransactionImage(MultipartFile file, Long transactionId) {
        return uploadFile(file, "transaction", transactionId.toString());
    }
    
    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl != null && fileUrl.startsWith(accessPath)) {
            String relativePath = fileUrl.substring(accessPath.length());
            Path path = Paths.get(uploadPath, relativePath);
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException("删除文件失败", e);
            }
        }
    }
    
    private String uploadFile(MultipartFile file, String type, String id) {
        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 创建目录
            String relativePath = type + "/" + id;
            Path uploadDir = Paths.get(uploadPath, relativePath);
            Files.createDirectories(uploadDir);
            
            // 保存文件
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // 返回访问URL
            return accessPath + "/" + relativePath + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
} 