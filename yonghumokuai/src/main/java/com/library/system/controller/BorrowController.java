package com.library.system.controller;

import com.library.system.entity.BorrowRecord;
import com.library.system.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    @GetMapping("/list")
    public List<BorrowRecord> findAll() {
        return borrowService.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<BorrowRecord> findByUser(@PathVariable Integer userId) {
        return borrowService.findByUserId(userId);
    }

    @PostMapping("/borrow")
    public void borrow(@RequestParam Integer userId, @RequestParam Integer bookId) {
        borrowService.borrowBook(userId, bookId);
    }

    @PostMapping("/return")
    public void returnBook(@RequestParam Integer recordId) {
        borrowService.returnBook(recordId);
    }
}
