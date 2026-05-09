package com.library.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.system.common.Result;
import com.library.system.entity.BorrowRecord;
import com.library.system.service.BorrowService;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    @GetMapping("/list")
    public Result<List<BorrowRecord>> findAll() {
        List<BorrowRecord> records = borrowService.findAll();
        return Result.success(records);
    }

    @GetMapping("/user/{userId}")
    public Result<List<BorrowRecord>> findByUser(@PathVariable Integer userId) {
        List<BorrowRecord> records = borrowService.findByUserId(userId);
        return Result.success(records);
    }

    @PostMapping("/borrow")
    public Result<String> borrow(@RequestParam Integer userId, @RequestParam Integer bookId) {
        try {
            borrowService.borrowBook(userId, bookId);
            return Result.success("借阅成功");
        } catch (IllegalStateException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/return")
    public Result<String> returnBook(@RequestParam Integer recordId) {
        try {
            borrowService.returnBook(recordId);
            return Result.success("归还成功");
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        borrowService.delete(id);
        return Result.success("删除成功");
    }
}
