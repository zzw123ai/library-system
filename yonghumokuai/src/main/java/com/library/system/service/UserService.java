package com.library.system.service;

import com.library.system.common.PasswordUtil;
import com.library.system.entity.User;
import com.library.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            String storedPassword = user.getPassword();
            if (storedPassword.length() == 60 && storedPassword.startsWith("$2a$")) {
                if (PasswordUtil.matches(password, storedPassword)) {
                    return user;
                }
            } else {
                if (user.getPassword().equals(password)) {
                    return user;
                }
            }
        }
        return null;
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }

    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public void insert(User user) {
        userMapper.insert(user);
    }

    public void update(User user) {
        userMapper.update(user);
    }

    public void delete(Integer id) {
        userMapper.delete(id);
    }

    public List<User> search(String username, String role) {
        return userMapper.search(username, role);
    }
}