package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.Book;
import com.example.demo.mapper.BookMapper;
import com.example.demo.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    
    @Override
    public Book getByIsbn(String isbn) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getIsbn, isbn);
        return getOne(wrapper);
    }
    
    @Override
    @Transactional
    public boolean updateBookStock(Long bookId, int change) {
        Book book = getById(bookId);
        if (book == null) {
            return false;
        }
        
        // 检查库存是否足够
        if (book.getAvailableCopies() + change < 0) {
            return false;
        }
        
        book.setAvailableCopies(book.getAvailableCopies() + change);
        return updateById(book);
    }
} 