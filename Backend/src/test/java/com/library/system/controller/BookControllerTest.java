package com.library.system.controller;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.system.entity.Book;
import com.library.system.service.BookService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookService bookService;

    @Test
    @Order(1)
    void testFindAll() throws Exception {
        mockMvc.perform(get("/api/book/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(2)
    void testFindById() throws Exception {
        mockMvc.perform(get("/api/book/find/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(3)
    void testFindByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/book/find/{id}", 999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("图书不存在"));
    }

    @Test
    @Order(4)
    void testSearch() throws Exception {
        mockMvc.perform(get("/api/book/search")
                        .param("title", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/book/search")
                        .param("author", "Author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/book/search")
                        .param("isbn", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/book/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/book/search").param("keyword", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(5)
    void testFindAvailable() throws Exception {
        mockMvc.perform(get("/api/book/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(6)
    void testAddBook() throws Exception {
        Book book = new Book();
        book.setTitle("Controller Test Book");
        book.setAuthor("Author");
        book.setIsbn("7777777777");
        book.setPublisher("Publisher");
        book.setQuantity(5);
        book.setAvailable(5);

        mockMvc.perform(post("/api/book/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("添加成功"));
    }

    @Test
    @Order(7)
    void testUpdateBook() throws Exception {
        Book book = new Book();
        book.setId(1);
        book.setTitle("Updated Book Title");
        book.setAuthor("Bruce Eckel");
        book.setIsbn("9787111213826");
        book.setPublisher("机械工业出版社");
        book.setQuantity(10);
        book.setAvailable(10);

        mockMvc.perform(put("/api/book/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    @Order(8)
    void testUpdateBookNotFound() throws Exception {
        Book book = new Book();
        book.setId(999);
        book.setTitle("Non-existent Book");
        book.setAuthor("Author");
        book.setIsbn("123");
        book.setPublisher("Publisher");
        book.setQuantity(1);
        book.setAvailable(1);

        mockMvc.perform(put("/api/book/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("图书不存在"));
    }

    @Test
    @Order(9)
    void testDeleteBook() throws Exception {
        Book book = new Book();
        book.setTitle("To Delete Book");
        book.setAuthor("Author");
        book.setIsbn("8888888888");
        book.setPublisher("Publisher");
        book.setQuantity(1);
        book.setAvailable(1);

        mockMvc.perform(post("/api/book/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk());

        List<Book> books = bookService.findByTitle("To Delete Book");
        Book inserted = books.get(0);

        mockMvc.perform(delete("/api/book/delete/{id}", inserted.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @Order(10)
    void testDeleteBookNotFound() throws Exception {
        mockMvc.perform(delete("/api/book/delete/{id}", 999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("图书不存在"));
    }
}