package com.library.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.library.system.entity.Book;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    @Order(1)
    void testFindAll() {
        assertNotNull(bookService.findAll());
    }

    @Test
    @Order(2)
    void testFindById() {
        Book book = bookService.findById(1);
        assertNotNull(book);
    }

    @Test
    @Order(3)
    void testFindByIdNonExistent() {
        Book book = bookService.findById(999);
        assertNull(book);
    }

    @Test
    @Order(4)
    void testFindByTitle() {
        assertNotNull(bookService.findByTitle("Java"));
    }

    @Test
    @Order(5)
    void testSearch() {
        assertNotNull(bookService.search("Java", null, null));
        assertNotNull(bookService.search(null, "Author", null));
        assertNotNull(bookService.search(null, null, "123"));
        assertNotNull(bookService.search("Java", "Author", "123"));
        assertNotNull(bookService.search(null, null, null));
    }

    @Test
    @Order(6)
    void testFindAvailable() {
        assertNotNull(bookService.findAvailable());
    }

    @Test
    @Order(7)
    void testInsert() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("1234567890");
        book.setPublisher("Test Publisher");
        book.setQuantity(5);
        book.setAvailable(5);
        
        bookService.insert(book);
        
        Book found = bookService.findById(book.getId());
        assertNotNull(found);
        assertEquals("Test Book", found.getTitle());
        assertEquals("Test Author", found.getAuthor());
        assertEquals("1234567890", found.getIsbn());
    }

    @Test
    @Order(8)
    void testUpdate() {
        Book book = bookService.findById(1);
        assertNotNull(book);
        
        String originalTitle = book.getTitle();
        book.setTitle("Updated Title");
        bookService.update(book);
        
        Book updated = bookService.findById(1);
        assertEquals("Updated Title", updated.getTitle());
        
        updated.setTitle(originalTitle);
        bookService.update(updated);
    }

    @Test
    @Order(9)
    void testDelete() {
        Book book = new Book();
        book.setTitle("To Delete Book");
        book.setAuthor("Author");
        book.setIsbn("9876543210");
        book.setPublisher("Publisher");
        book.setQuantity(1);
        book.setAvailable(1);
        bookService.insert(book);
        
        Book inserted = bookService.findById(book.getId());
        assertNotNull(inserted);
        
        bookService.delete(inserted.getId());
        
        Book deleted = bookService.findById(inserted.getId());
        assertNull(deleted);
    }

    @Test
    @Order(10)
    void testBorrowBook() {
        Book book = new Book();
        book.setTitle("Borrow Test Book");
        book.setAuthor("Author");
        book.setIsbn("1111111111");
        book.setPublisher("Publisher");
        book.setQuantity(2);
        book.setAvailable(2);
        bookService.insert(book);
        
        int initialAvailable = book.getAvailable();
        
        boolean result = bookService.borrowBook(book.getId());
        assertTrue(result);
        
        Book borrowed = bookService.findById(book.getId());
        assertEquals(initialAvailable - 1, borrowed.getAvailable());
    }

    @Test
    @Order(11)
    void testReturnBook() {
        Book book = new Book();
        book.setTitle("Return Test Book");
        book.setAuthor("Author");
        book.setIsbn("2222222222");
        book.setPublisher("Publisher");
        book.setQuantity(2);
        book.setAvailable(1);
        bookService.insert(book);
        
        int initialAvailable = book.getAvailable();
        
        bookService.returnBook(book.getId());
        
        Book returned = bookService.findById(book.getId());
        assertEquals(initialAvailable + 1, returned.getAvailable());
    }
}
