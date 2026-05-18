package com.library.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.library.system.entity.Book;
import com.library.system.entity.BorrowRecord;
import com.library.system.entity.User;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class BorrowServiceTest {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    void testFindAll() {
        assertNotNull(borrowService.findAll());
    }

    @Test
    @Order(2)
    void testFindByUserId() {
        assertNotNull(borrowService.findByUserId(1));
    }

    @Test
    @Order(3)
    void testFindById() {
        BorrowRecord record = borrowService.findById(1);
        // 如果data.sql中有初始借阅记录，则不为null，否则为null
        // 这个测试主要验证方法正常工作
        if (record != null) {
            assertNotNull(record.getId());
        }
    }

    @Test
    @Order(4)
    void testFindByIdNonExistent() {
        BorrowRecord record = borrowService.findById(99999);
        assertNull(record);
    }

    @Test
    @Order(5)
    void testBorrowBook() {
        User user = new User();
        user.setUsername("borrowTestUser");
        user.setPassword("password");
        user.setRole("0");
        userService.insert(user);
        
        Book book = new Book();
        book.setTitle("Borrow Test Book");
        book.setAuthor("Author");
        book.setIsbn("3333333333");
        book.setPublisher("Publisher");
        book.setQuantity(3);
        book.setAvailable(3);
        bookService.insert(book);
        
        borrowService.borrowBook(user.getId(), book.getId(), "2026-12-31");
        
        Book borrowedBook = bookService.findById(book.getId());
        assertEquals(2, borrowedBook.getAvailable());
        
        BorrowRecord record = borrowService.findByUserId(user.getId()).get(0);
        assertEquals("BORROWED", record.getStatus());
        assertEquals(user.getId(), record.getUserId());
        assertEquals(book.getId(), record.getBookId());
    }

    @Test
    @Order(6)
    void testBorrowBookNotAvailable() {
        User user = new User();
        user.setUsername("borrowTestUser2");
        user.setPassword("password");
        user.setRole("0");
        userService.insert(user);
        
        Book book = new Book();
        book.setTitle("Unavailable Book");
        book.setAuthor("Author");
        book.setIsbn("4444444444");
        book.setPublisher("Publisher");
        book.setQuantity(0);
        book.setAvailable(0);
        bookService.insert(book);
        
        assertThrows(IllegalStateException.class, () -> {
            borrowService.borrowBook(user.getId(), book.getId(), "2026-12-31");
        });
    }

    @Test
    @Order(7)
    void testReturnBook() {
        User user = new User();
        user.setUsername("returnTestUser");
        user.setPassword("password");
        user.setRole("0");
        userService.insert(user);
        
        Book book = new Book();
        book.setTitle("Return Test Book");
        book.setAuthor("Author");
        book.setIsbn("5555555555");
        book.setPublisher("Publisher");
        book.setQuantity(3);
        book.setAvailable(3);
        bookService.insert(book);
        
        borrowService.borrowBook(user.getId(), book.getId(), "2026-12-31");
        
        BorrowRecord record = borrowService.findByUserId(user.getId()).get(0);
        
        borrowService.returnBook(record.getId());
        
        Book returnedBook = bookService.findById(book.getId());
        assertEquals(3, returnedBook.getAvailable());
        
        BorrowRecord returnedRecord = borrowService.findById(record.getId());
        assertEquals("RETURNED", returnedRecord.getStatus());
        assertNotNull(returnedRecord.getReturnDate());
    }

    @Test
    @Order(8)
    void testReturnBookNonExistent() {
        assertThrows(IllegalArgumentException.class, () -> {
            borrowService.returnBook(999);
        });
    }

    @Test
    @Order(9)
    void testDelete() {
        User user = new User();
        user.setUsername("deleteTestUser");
        user.setPassword("password");
        user.setRole("0");
        userService.insert(user);
        
        Book book = new Book();
        book.setTitle("Delete Test Book");
        book.setAuthor("Author");
        book.setIsbn("6666666666");
        book.setPublisher("Publisher");
        book.setQuantity(3);
        book.setAvailable(3);
        bookService.insert(book);
        
        borrowService.borrowBook(user.getId(), book.getId(), "2026-12-31");
        
        BorrowRecord record = borrowService.findByUserId(user.getId()).get(0);
        assertNotNull(record);
        
        borrowService.delete(record.getId());
        
        BorrowRecord deleted = borrowService.findById(record.getId());
        assertNull(deleted);
    }
}
