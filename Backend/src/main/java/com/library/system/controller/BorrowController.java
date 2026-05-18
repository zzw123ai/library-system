package com.library.system.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.system.common.AuthUser;
import com.library.system.common.OverdueReminderVO;
import com.library.system.common.Result;
import com.library.system.config.AuthFilter;
import com.library.system.entity.BorrowRecord;
import com.library.system.service.BorrowService;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    @GetMapping("/list")
    public Result<List<BorrowRecord>> findAll(HttpServletRequest request) {
        AuthUser user = currentUser(request);
        if (user == null) {
            return Result.unauthorized("请先登录");
        }
        List<BorrowRecord> records = user.isAdmin()
                ? borrowService.findAll()
                : borrowService.findByUserId(user.getId());
        return Result.success(records);
    }

    /**
     * 逾期提醒：先刷新逾期状态，再返回逾期列表与数量。
     * @param userId 可选；传入则只查该读者的逾期记录，不传则返回全部（管理员用）
     */
    @GetMapping("/overdue/reminder")
    public Result<OverdueReminderVO> overdueReminder(@RequestParam(required = false) Integer userId,
                                                     HttpServletRequest request) {
        AuthUser user = currentUser(request);
        if (user == null) {
            return Result.unauthorized("请先登录");
        }
        // 读者仅可查看自己的逾期提醒
        Integer queryUserId = user.isAdmin() ? userId : user.getId();
        OverdueReminderVO vo = borrowService.getOverdueReminder(queryUserId);
        return Result.success(vo);
    }

    @GetMapping("/user/{userId}")
    public Result<List<BorrowRecord>> findByUser(@PathVariable Integer userId, HttpServletRequest request) {
        AuthUser user = currentUser(request);
        if (user == null) {
            return Result.unauthorized("请先登录");
        }
        if (!user.isAdmin() && !user.getId().equals(userId)) {
            return Result.error(403, "无权访问其他用户借阅记录");
        }
        List<BorrowRecord> records = borrowService.findByUserId(userId);
        return Result.success(records);
    }

    @PostMapping("/borrow")
    public Result<String> borrow(@RequestParam Integer userId,
                                 @RequestParam Integer bookId,
                                 @RequestParam String dueDate,
                                 HttpServletRequest request) {
        AuthUser user = currentUser(request);
        if (user == null) {
            return Result.unauthorized("请先登录");
        }
        Integer actualUserId = user.isAdmin() ? userId : user.getId();
        try {
            borrowService.borrowBook(actualUserId, bookId, dueDate);
            return Result.success("借阅成功");
        } catch (IllegalStateException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/return")
    public Result<String> returnBook(@RequestParam Integer recordId, HttpServletRequest request) {
        AuthUser user = currentUser(request);
        if (user == null) {
            return Result.unauthorized("请先登录");
        }
        BorrowRecord record = borrowService.findById(recordId);
        if (record == null) {
            return Result.error(400, "借阅记录不存在");
        }
        if (!user.isAdmin() && !user.getId().equals(record.getUserId())) {
            return Result.error(403, "仅可归还自己的借阅记录");
        }
        try {
            borrowService.returnBook(recordId);
            return Result.success("归还成功");
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id, HttpServletRequest request) {
        AuthUser user = currentUser(request);
        if (user == null) {
            return Result.unauthorized("请先登录");
        }
        if (!user.isAdmin()) {
            return Result.error(403, "仅管理员可操作");
        }
        borrowService.delete(id);
        return Result.success("删除成功");
    }

    private AuthUser currentUser(HttpServletRequest request) {
        return (AuthUser) request.getAttribute(AuthFilter.AUTH_USER_ATTR);
    }
}
