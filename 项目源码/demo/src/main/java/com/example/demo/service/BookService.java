package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.entity.Book;

public interface BookService extends IService<Book> {
    // 根据ISBN查询图书
    Book getByIsbn(String isbn);
    
    // 更新图书库存
    boolean updateBookStock(Long bookId, int change);
} 