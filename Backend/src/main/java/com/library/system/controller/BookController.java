package com.library.system.controller;

import com.library.system.common.Result;
import com.library.system.common.AuthUser;
import com.library.system.config.AuthFilter;
import com.library.system.entity.Book;
import com.library.system.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
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
    public Result<List<Book>> search(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) String title,
                                     @RequestParam(required = false) String author,
                                     @RequestParam(required = false) String isbn) {
        List<Book> books;
        if (keyword != null && !keyword.isBlank()) {
            books = bookService.searchByKeyword(keyword);
        } else {
            books = bookService.search(title, author, isbn);
        }
        return Result.success(books);
    }

    @GetMapping("/available")
    public Result<List<Book>> findAvailable() {
        List<Book> books = bookService.findAvailable();
        return Result.success(books);
    }

    @PostMapping("/add")
    public Result<String> insert(@RequestBody Book book, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        if (book.getAvailable() == null) {
            book.setAvailable(book.getQuantity());
        }
        bookService.insert(book);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Book book, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        Book existingBook = bookService.findById(book.getId());
        if (existingBook == null) {
            return Result.notFound("图书不存在");
        }
        bookService.update(book);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        Book book = bookService.findById(id);
        if (book == null) {
            return Result.notFound("图书不存在");
        }
        bookService.delete(id);
        return Result.success("删除成功");
    }

    @GetMapping("/duplicates")
    public Result<List<Book>> findDuplicates(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可访问");
        }
        List<Book> duplicates = bookService.findDuplicateBooks();
        return Result.success(duplicates);
    }

    @GetMapping("/duplicates/count")
    public Result<Integer> countDuplicates(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可访问");
        }
        int count = bookService.countDuplicateBooks();
        return Result.success(count);
    }

    @DeleteMapping("/duplicates")
    public Result<String> removeDuplicates(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        int count = bookService.countDuplicateBooks();
        if (count == 0) {
            return Result.success("没有重复书籍");
        }
        bookService.removeDuplicateBooks();
        return Result.success("已删除 " + count + " 本重复书籍");
    }

    private boolean isAdmin(HttpServletRequest request) {
        AuthUser user = (AuthUser) request.getAttribute(AuthFilter.AUTH_USER_ATTR);
        return user != null && user.isAdmin();
    }
}