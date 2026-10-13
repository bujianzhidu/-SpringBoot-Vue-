package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Reader;

public interface ReaderService {

    public Reader getByReaderNo(String readerNo);


    public boolean canBorrow(Long readerId);
}
