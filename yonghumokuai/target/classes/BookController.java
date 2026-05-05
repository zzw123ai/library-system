package com.library.system.controller;

import com.library.system.entity.Book;
import com.library.system.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/list")
    public List<Book> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/find/{id}")
    public Book findById(@PathVariable Integer id) {
        return bookService.findById(id);
    }

    @GetMapping("/search")
    public List<Book> findByTitle(@RequestParam String title) {
        return bookService.findByTitle(title);
    }

    @PostMapping("/add")
    public void insert(@RequestBody Book book) {
        bookService.insert(book);
    }

    @PutMapping("/update")
    public void update(@RequestBody Book book) {
        bookService.update(book);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id) {
        bookService.delete(id);
    }
}