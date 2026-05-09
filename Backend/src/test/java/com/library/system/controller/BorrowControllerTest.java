package com.library.system.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.library.system.entity.Book;
import com.library.system.entity.User;
import com.library.system.service.BookService;
import com.library.system.service.BorrowService;
import com.library.system.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class BorrowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowService borrowService;

    @Test
    @Order(1)
    void testFindAll() throws Exception {
        mockMvc.perform(get("/api/borrow/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(2)
    void testFindByUser() throws Exception {
        mockMvc.perform(get("/api/borrow/user/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(3)
    void testBorrow() throws Exception {
        User user = new User();
        user.setUsername("borrowControllerTest123");
        user.setPassword("password");
        user.setRole("0");
        userService.insert(user);

        Book book = new Book();
        book.setTitle("Borrow Controller Test");
        book.setAuthor("Author");
        book.setIsbn("9999999999");
        book.setPublisher("Publisher");
        book.setQuantity(3);
        book.setAvailable(3);
        bookService.insert(book);

        mockMvc.perform(post("/api/borrow/borrow")
                        .param("userId", String.valueOf(user.getId()))
                        .param("bookId", String.valueOf(book.getId()))
                        .param("dueDate", "2026-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("借阅成功"));
    }

    @Test
    @Order(4)
    void testBorrowNotAvailable() throws Exception {
        User user = new User();
        user.setUsername("borrowNotAvailableTest456");
        user.setPassword("password");
        user.setRole("0");
        userService.insert(user);

        Book book = new Book();
        book.setTitle("Not Available Book");
        book.setAuthor("Author");
        book.setIsbn("0000000000");
        book.setPublisher("Publisher");
        book.setQuantity(0);
        book.setAvailable(0);
        bookService.insert(book);

        mockMvc.perform(post("/api/borrow/borrow")
                        .param("userId", String.valueOf(user.getId()))
                        .param("bookId", String.valueOf(book.getId()))
                        .param("dueDate", "2026-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("图书不可借"));
    }

    @Test
    @Order(5)
    void testReturnBook() throws Exception {
        User user = new User();
        user.setUsername("returnControllerTest789");
        user.setPassword("password");
        user.setRole("0");
        userService.insert(user);

        Book book = new Book();
        book.setTitle("Return Controller Test");
        book.setAuthor("Author");
        book.setIsbn("1010101010");
        book.setPublisher("Publisher");
        book.setQuantity(3);
        book.setAvailable(3);
        bookService.insert(book);

        mockMvc.perform(post("/api/borrow/borrow")
                        .param("userId", String.valueOf(user.getId()))
                        .param("bookId", String.valueOf(book.getId()))
                        .param("dueDate", "2026-12-31"))
                .andExpect(status().isOk());

        Integer recordId = borrowService.findByUserId(user.getId()).get(0).getId();

        mockMvc.perform(post("/api/borrow/return")
                        .param("recordId", String.valueOf(recordId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("归还成功"));
    }

    @Test
    @Order(6)
    void testReturnBookNotFound() throws Exception {
        mockMvc.perform(post("/api/borrow/return")
                        .param("recordId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @Order(7)
    void testDelete() throws Exception {
        User user = new User();
        user.setUsername("deleteBorrowTest000");
        user.setPassword("password");
        user.setRole("0");
        userService.insert(user);

        Book book = new Book();
        book.setTitle("Delete Borrow Test");
        book.setAuthor("Author");
        book.setIsbn("1110001110");
        book.setPublisher("Publisher");
        book.setQuantity(3);
        book.setAvailable(3);
        bookService.insert(book);

        mockMvc.perform(post("/api/borrow/borrow")
                        .param("userId", String.valueOf(user.getId()))
                        .param("bookId", String.valueOf(book.getId()))
                        .param("dueDate", "2026-12-31"))
                .andExpect(status().isOk());

        Integer recordId = borrowService.findByUserId(user.getId()).get(0).getId();

        mockMvc.perform(delete("/api/borrow/delete/{id}", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }
}