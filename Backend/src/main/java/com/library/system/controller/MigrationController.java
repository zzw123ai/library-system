package com.library.system.controller;

import com.library.system.common.AuthUser;
import com.library.system.common.Result;
import com.library.system.config.AuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/migration")
public class MigrationController {

    private final JdbcTemplate jdbcTemplate;

    public MigrationController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/add-due-date")
    public Result<String> addDueDateColumn(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        try {
            jdbcTemplate.execute("ALTER TABLE borrow_record ADD COLUMN due_date DATE");
            return Result.success("Success: due_date column added");
        } catch (Exception e) {
            return Result.error("Error: " + e.getMessage());
        }
    }

    @PostMapping("/update-password-length")
    public Result<String> updatePasswordLength(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        try {
            jdbcTemplate.execute("ALTER TABLE `user` MODIFY COLUMN password VARCHAR(255) NOT NULL");
            return Result.success("Success: password column length updated");
        } catch (Exception e) {
            return Result.error("Error: " + e.getMessage());
        }
    }

    @PostMapping("/remove-duplicate-admins")
    public Result<String> removeDuplicateAdmins(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        try {
            jdbcTemplate.execute("DELETE FROM `user` WHERE role = '1' AND id != 1");
            return Result.success("Success: duplicate admins removed, only admin with id=1 remains");
        } catch (Exception e) {
            return Result.error("Error: " + e.getMessage());
        }
    }

    @PostMapping("/remove-duplicate-users")
    public Result<String> removeDuplicateUsers(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        try {
            jdbcTemplate.execute("DELETE u1 FROM `user` u1 JOIN `user` u2 ON u1.username = u2.username AND u1.role = '0' AND u2.role = '0' AND u1.id > u2.id");
            return Result.success("Success: duplicate ordinary users removed");
        } catch (Exception e) {
            return Result.error("Error: " + e.getMessage());
        }
    }

    @PostMapping("/resequence-user-ids")
    public Result<String> resequenceUserIds(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        try {
            jdbcTemplate.execute("SET @new_id = 0;");
            jdbcTemplate.execute("UPDATE `user` SET id = (@new_id := @new_id + 1) ORDER BY id;");
            jdbcTemplate.execute("ALTER TABLE `user` AUTO_INCREMENT = 1;");
            return Result.success("Success: user ids resequenced");
        } catch (Exception e) {
            return Result.error("Error: " + e.getMessage());
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        AuthUser user = (AuthUser) request.getAttribute(AuthFilter.AUTH_USER_ATTR);
        return user != null && user.isAdmin();
    }
}
