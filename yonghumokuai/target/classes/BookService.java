package com.library.system.service;

import com.library.system.entity.Book;
import com.library.system.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookMapper bookMapper;

    public List<Book> findAll() {
        return bookMapper.findAll();
    }

    public Book findById(Integer id) {
        return bookMapper.findById(id);
    }

    public List<Book> findByTitle(String title) {
        return bookMapper.findByTitle(title);
    }

    public void insert(Book book) {
        bookMapper.insert(book);
    }

    public void update(Book book) {
        bookMapper.update(book);
    }

    public void delete(Integer id) {
        bookMapper.delete(id);
    }
}