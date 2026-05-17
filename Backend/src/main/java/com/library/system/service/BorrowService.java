package com.library.system.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.system.entity.Book;
import com.library.system.entity.BorrowRecord;
import com.library.system.entity.User;
import com.library.system.mapper.BookMapper;
import com.library.system.mapper.BorrowMapper;
import com.library.system.mapper.UserMapper;

@Service
public class BorrowService {
    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<BorrowRecord> findAll() {
        List<BorrowRecord> records = borrowMapper.findAll();
        enrichRecords(records);
        return records;
    }

    public List<BorrowRecord> findByUserId(Integer userId) {
        List<BorrowRecord> records = borrowMapper.findByUserId(userId);
        enrichRecords(records);
        return records;
    }

    public BorrowRecord findById(Integer id) {
        BorrowRecord record = borrowMapper.findById(id);
        if (record != null) {
            enrichRecord(record);
        }
        return record;
    }

    private void enrichRecords(List<BorrowRecord> records) {
        for (BorrowRecord record : records) {
            enrichRecord(record);
        }
    }

    private void enrichRecord(BorrowRecord record) {
        if (record.getUserId() != null) {
            User user = userMapper.findById(record.getUserId());
            if (user != null) {
                record.setUsername(user.getUsername());
            }
        }
        if (record.getBookId() != null) {
            Book book = bookMapper.findById(record.getBookId());
            if (book != null) {
                record.setBookTitle(book.getTitle());
            }
        }
    }

    public void borrowBook(Integer userId, Integer bookId, String dueDate) {
        Book book = bookMapper.findById(bookId);
        if (book == null || book.getAvailable() == null || book.getAvailable() <= 0) {
            throw new IllegalStateException("图书不可借");
        }
        book.setAvailable(book.getAvailable() - 1);
        bookMapper.update(book);

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now().format(fmt));
        record.setDueDate(dueDate);
        record.setStatus("BORROWED");
        borrowMapper.insert(record);
    }

    public void returnBook(Integer recordId) {
        BorrowRecord record = borrowMapper.findById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("借阅记录不存在");
        }
        if ("RETURNED".equals(record.getStatus())) {
            return;
        }
        Book book = bookMapper.findById(record.getBookId());
        if (book != null) {
            book.setAvailable((book.getAvailable() == null ? 0 : book.getAvailable()) + 1);
            bookMapper.update(book);
        }
        record.setReturnDate(LocalDateTime.now().format(fmt));
        record.setStatus("RETURNED");
        borrowMapper.update(record);
    }

    public void delete(Integer id) {
        borrowMapper.delete(id);
        List<Integer> ids = borrowMapper.getAllIds();
        for (int i = ids.size() - 1; i >= 0; i--) {
            int oldId = ids.get(i);
            int newId = i + 1;
            if (oldId != newId) {
                borrowMapper.updateId(oldId, newId);
            }
        }
        borrowMapper.resetAutoIncrement();
    }
}
