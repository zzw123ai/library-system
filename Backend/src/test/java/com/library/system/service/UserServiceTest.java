package com.library.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.library.system.entity.User;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    void testFindAll() {
        assertNotNull(userService.findAll());
    }

    @Test
    @Order(2)
    void testFindById() {
        User user = userService.findById(1);
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    @Order(3)
    void testFindByIdNonExistent() {
        User user = userService.findById(999);
        assertNull(user);
    }

    @Test
    @Order(4)
    void testFindByUsername() {
        User user = userService.findByUsername("admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    @Order(5)
    void testLogin() {
        User user = userService.login("admin", "password");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("1", user.getRole());
    }

    @Test
    @Order(6)
    void testLoginWithWrongPassword() {
        User user = userService.login("admin", "wrongpassword");
        assertNull(user);
    }

    @Test
    @Order(7)
    void testLoginWithNonExistentUser() {
        User user = userService.login("nonexistent", "password");
        assertNull(user);
    }

    @Test
    @Order(8)
    void testInsert() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole("0");
        
        userService.insert(user);
        
        User found = userService.findByUsername("testuser");
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
        assertEquals("0", found.getRole());
    }

    @Test
    @Order(9)
    void testUpdate() {
        User user = userService.findById(1);
        assertNotNull(user);
        
        String originalUsername = user.getUsername();
        user.setUsername("admin_updated");
        userService.update(user);
        
        User updated = userService.findById(1);
        assertEquals("admin_updated", updated.getUsername());
        
        updated.setUsername(originalUsername);
        userService.update(updated);
    }

    @Test
    @Order(10)
    void testDelete() {
        User user = new User();
        user.setUsername("todelete");
        user.setPassword("password");
        user.setRole("0");
        userService.insert(user);
        
        User inserted = userService.findByUsername("todelete");
        assertNotNull(inserted);
        
        userService.delete(inserted.getId());
        
        User deleted = userService.findById(inserted.getId());
        assertNull(deleted);
    }

    @Test
    @Order(11)
    void testSearch() {
        assertNotNull(userService.search("admin", null));
        assertNotNull(userService.search(null, "1"));
        assertNotNull(userService.search("admin", "1"));
        assertNotNull(userService.search(null, null));
    }
}
