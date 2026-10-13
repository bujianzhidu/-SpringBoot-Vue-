package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.BorrowRecord;
import com.example.demo.entity.Reader;
import com.example.demo.mapper.BorrowRecordMapper;
import com.example.demo.mapper.ReaderMapper;
import com.example.demo.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReaderServiceImpl extends ServiceImpl<ReaderMapper, Reader> implements ReaderService {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Override
    public Reader getByReaderNo(String readerNo) {
        LambdaQueryWrapper<Reader> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reader::getReaderNo, readerNo);
        return getOne(wrapper);
    }

    @Override
    public boolean canBorrow(Long readerId) {
        Reader reader = getById(readerId);
        if (reader == null || !reader.getStatus()) {
            return false;
        }

        // 检查当前借阅数量是否超过上限
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getReaderId, readerId)
                .eq(BorrowRecord::getStatus, "BORROWED");
        long borrowedCount = borrowRecordMapper.selectCount(wrapper);

        return borrowedCount < reader.getMaxBorrowBooks();
    }
}
