package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.BorrowRecord;

public interface BorrowRecordService extends IService<BorrowRecord> {
    // 借书
    boolean borrowBook(Long readerId, Long bookId);
    
    // 还书
    boolean returnBook(Long recordId);
    
    // 续借
    boolean renewBook(Long recordId);
} 