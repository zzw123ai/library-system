package com.library.system.service;

import com.library.system.entity.BorrowRecord;
import com.library.system.entity.Book;
import com.library.system.mapper.BorrowMapper;
import com.library.system.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BorrowService {
    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private BookMapper bookMapper;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<BorrowRecord> findAll() {
        return borrowMapper.findAll();
    }

    public List<BorrowRecord> findByUserId(Integer userId) {
        return borrowMapper.findByUserId(userId);
    }

    public BorrowRecord findById(Integer id) {
        return borrowMapper.findById(id);
    }

    public void borrowBook(Integer userId, Integer bookId) {
        Book book = bookMapper.findById(bookId);
        if (book == null || book.getAvailable() == null || book.getAvailable() <= 0) {
            throw new IllegalStateException("图书不可借");
        }
        // 减少可借数量
        book.setAvailable(book.getAvailable() - 1);
        bookMapper.update(book);

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now().format(fmt));
        record.setStatus("borrowed");
        borrowMapper.insert(record);
    }

    public void returnBook(Integer recordId) {
        BorrowRecord record = borrowMapper.findById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("借阅记录不存在");
        }
        if ("returned".equals(record.getStatus())) {
            return; // 已归还
        }
        // 更新图书剩余
        Book book = bookMapper.findById(record.getBookId());
        if (book != null) {
            book.setAvailable((book.getAvailable() == null ? 0 : book.getAvailable()) + 1);
            bookMapper.update(book);
        }
        record.setReturnDate(LocalDateTime.now().format(fmt));
        record.setStatus("returned");
        borrowMapper.update(record);
    }
}
