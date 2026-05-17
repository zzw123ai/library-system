package com.library.system.controller;

import com.library.system.common.Result;
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
    public Result<List<Book>> findAll() {
        List<Book> books = bookService.findAll();
        return Result.success(books);
    }

    @GetMapping("/find/{id}")
    public Result<Book> findById(@PathVariable Integer id) {
        Book book = bookService.findById(id);
        if (book != null) {
            return Result.success(book);
        }
        return Result.notFound("图书不存在");
    }

    @GetMapping("/search")
    public Result<List<Book>> search(@RequestParam(required = false) String title,
                                     @RequestParam(required = false) String author,
                                     @RequestParam(required = false) String isbn) {
        List<Book> books = bookService.search(title, author, isbn);
        return Result.success(books);
    }

    @GetMapping("/available")
    public Result<List<Book>> findAvailable() {
        List<Book> books = bookService.findAvailable();
        return Result.success(books);
    }

    @PostMapping("/add")
    public Result<String> insert(@RequestBody Book book) {
        if (book.getAvailable() == null) {
            book.setAvailable(book.getQuantity());
        }
        bookService.insert(book);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Book book) {
        Book existingBook = bookService.findById(book.getId());
        if (existingBook == null) {
            return Result.notFound("图书不存在");
        }
        bookService.update(book);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        Book book = bookService.findById(id);
        if (book == null) {
            return Result.notFound("图书不存在");
        }
        bookService.delete(id);
        return Result.success("删除成功");
    }

    @GetMapping("/duplicates")
    public Result<List<Book>> findDuplicates() {
        List<Book> duplicates = bookService.findDuplicateBooks();
        return Result.success(duplicates);
    }

    @GetMapping("/duplicates/count")
    public Result<Integer> countDuplicates() {
        int count = bookService.countDuplicateBooks();
        return Result.success(count);
    }

    @DeleteMapping("/duplicates")
    public Result<String> removeDuplicates() {
        int count = bookService.countDuplicateBooks();
        if (count == 0) {
            return Result.success("没有重复书籍");
        }
        bookService.removeDuplicateBooks();
        return Result.success("已删除 " + count + " 本重复书籍");
    }
}