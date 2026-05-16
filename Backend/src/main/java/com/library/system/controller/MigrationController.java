package com.library.system.controller;

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
    public String addDueDateColumn() {
        try {
            jdbcTemplate.execute("ALTER TABLE borrow_record ADD COLUMN due_date DATE");
            return "Success: due_date column added";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/update-password-length")
    public String updatePasswordLength() {
        try {
            jdbcTemplate.execute("ALTER TABLE `user` MODIFY COLUMN password VARCHAR(255) NOT NULL");
            return "Success: password column length updated";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/remove-duplicate-admins")
    public String removeDuplicateAdmins() {
        try {
            jdbcTemplate.execute("DELETE FROM `user` WHERE role = '1' AND id != 1");
            return "Success: duplicate admins removed, only admin with id=1 remains";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/remove-duplicate-users")
    public String removeDuplicateUsers() {
        try {
            jdbcTemplate.execute("DELETE u1 FROM `user` u1 JOIN `user` u2 ON u1.username = u2.username AND u1.role = '0' AND u2.role = '0' AND u1.id > u2.id");
            return "Success: duplicate ordinary users removed";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/resequence-user-ids")
    public String resequenceUserIds() {
        try {
            jdbcTemplate.execute("SET @new_id = 0;");
            jdbcTemplate.execute("UPDATE `user` SET id = (@new_id := @new_id + 1) ORDER BY id;");
            jdbcTemplate.execute("ALTER TABLE `user` AUTO_INCREMENT = 1;");
            return "Success: user ids resequenced";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
