package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 上传用户头像
     * @param file 图片文件
     * @param userId 用户ID
     * @return 文件访问URL
     */
    String uploadAvatar(MultipartFile file, Long userId);
    
    /**
     * 上传交易凭证图片
     * @param file 图片文件
     * @param transactionId 交易ID
     * @return 文件访问URL
     */
    String uploadTransactionImage(MultipartFile file, Long transactionId);
    
    /**
     * 删除文件
     * @param fileUrl 文件URL
     */
    void deleteFile(String fileUrl);
} 