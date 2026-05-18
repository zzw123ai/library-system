package com.library.system.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import com.library.system.common.OverdueReminderVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<BorrowRecord> findAll() {
        refreshOverdueStatus();
        List<BorrowRecord> records = borrowMapper.findAll();
        enrichRecords(records);
        return records;
    }

    public List<BorrowRecord> findByUserId(Integer userId) {
        refreshOverdueStatus();
        List<BorrowRecord> records = borrowMapper.findByUserId(userId);
        enrichRecords(records);
        return records;
    }

    /**
     * 按角色返回借阅列表，并按书名、用户名模糊筛选（keyword 为空则不过滤）。
     */
    public List<BorrowRecord> listForCurrentUser(boolean admin, Integer userId, String keyword) {
        List<BorrowRecord> records = admin ? findAll() : findByUserId(userId);
        return filterByKeyword(records, keyword);
    }

    private List<BorrowRecord> filterByKeyword(List<BorrowRecord> records, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return records;
        }
        String kw = keyword.trim().toLowerCase();
        return records.stream()
                .filter(r -> fieldContains(r.getBookTitle(), kw) || fieldContains(r.getUsername(), kw))
                .collect(Collectors.toList());
    }

    private boolean fieldContains(String value, String kw) {
        return value != null && value.toLowerCase().contains(kw);
    }

    /**
     * 扫描未归还记录：应还日期早于今天则标记为 OVERDUE 并写回数据库。
     */
    public void refreshOverdueStatus() {
        LocalDate today = LocalDate.now();
        List<BorrowRecord> all = borrowMapper.findAll();
        for (BorrowRecord record : all) {
            if (record == null || "RETURNED".equals(record.getStatus())) {
                continue;
            }
            LocalDate due = parseDueDate(record.getDueDate());
            if (due == null) {
                continue;
            }
            if (today.isAfter(due)) {
                if (!"OVERDUE".equals(record.getStatus())) {
                    borrowMapper.updateStatus(record.getId(), "OVERDUE");
                    record.setStatus("OVERDUE");
                }
            } else if ("OVERDUE".equals(record.getStatus())) {
                // 应还日尚未到但曾被标为逾期时，恢复为借阅中
                borrowMapper.updateStatus(record.getId(), "BORROWED");
                record.setStatus("BORROWED");
            }
        }
    }

    public OverdueReminderVO getOverdueReminder(Integer userId) {
        refreshOverdueStatus();
        List<BorrowRecord> records;
        if (userId != null) {
            records = borrowMapper.findOverdueByUserId(userId);
        } else {
            records = borrowMapper.findOverdueAll();
        }
        enrichRecords(records);
        attachOverdueDays(records);
        return new OverdueReminderVO(records.size(), records);
    }

    private void attachOverdueDays(List<BorrowRecord> records) {
        LocalDate today = LocalDate.now();
        for (BorrowRecord record : records) {
            LocalDate due = parseDueDate(record.getDueDate());
            if (due != null && today.isAfter(due)) {
                record.setOverdueDays((int) ChronoUnit.DAYS.between(due, today));
            } else {
                record.setOverdueDays(0);
            }
        }
    }

    private LocalDate parseDueDate(String dueDate) {
        if (dueDate == null || dueDate.isBlank()) {
            return null;
        }
        String datePart = dueDate.length() >= 10 ? dueDate.substring(0, 10) : dueDate;
        try {
            return LocalDate.parse(datePart, dateFmt);
        } catch (DateTimeParseException e) {
            return null;
        }
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
        if ("OVERDUE".equals(record.getStatus())) {
            LocalDate due = parseDueDate(record.getDueDate());
            if (due != null) {
                LocalDate today = LocalDate.now();
                if (today.isAfter(due)) {
                    record.setOverdueDays((int) ChronoUnit.DAYS.between(due, today));
                }
            }
        }
    }

    @Transactional
    public void borrowBook(Integer userId, Integer bookId, String dueDate) {
        int decreased = bookMapper.decreaseAvailable(bookId);
        if (decreased <= 0) {
            throw new IllegalStateException("图书不可借");
        }

        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now().format(fmt));
        record.setDueDate(dueDate);
        record.setStatus("BORROWED");
        borrowMapper.insert(record);
    }

    @Transactional
    public void returnBook(Integer recordId) {
        BorrowRecord record = borrowMapper.findById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("借阅记录不存在");
        }
        if ("RETURNED".equals(record.getStatus())) {
            return;
        }
        // 逾期、借阅中均可归还
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
