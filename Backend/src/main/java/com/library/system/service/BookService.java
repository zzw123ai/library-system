package com.library.system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.system.entity.Book;
import com.library.system.mapper.BookMapper;

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

    public List<Book> search(String title, String author, String isbn) {
        return bookMapper.search(title, author, isbn);
    }

    /** 单关键词模糊匹配书名、作者、ISBN、出版社（OR） */
    public List<Book> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return bookMapper.searchByKeyword(keyword.trim());
    }

    public List<Book> findAvailable() {
        return bookMapper.findAvailable();
    }

    public boolean borrowBook(Integer bookId) {
        int updated = bookMapper.decreaseAvailable(bookId);
        return updated > 0;
    }

    public void returnBook(Integer bookId) {
        bookMapper.increaseAvailable(bookId);
    }

    public void insert(Book book) {
        if (book.getAvailable() == null && book.getQuantity() != null) {
            book.setAvailable(book.getQuantity());
        }
        if (book.getQuantity() == null) {
            book.setQuantity(book.getAvailable() != null ? book.getAvailable() : 1);
        }
        if (book.getAvailable() == null) {
            book.setAvailable(book.getQuantity());
        }
        bookMapper.insert(book);
    }

    public void update(Book book) {
        bookMapper.update(book);
    }

    public void delete(Integer id) {
        bookMapper.updateBorrowRecordBookIds();
        bookMapper.delete(id);
        bookMapper.renumberIds();
    }

    public List<Book> findDuplicateBooks() {
        return bookMapper.findDuplicateBooks();
    }

    public int countDuplicateBooks() {
        return bookMapper.countDuplicateBooks();
    }

    public void removeDuplicateBooks() {
        bookMapper.deleteDuplicateBooks();
        bookMapper.renumberIds();
    }
}