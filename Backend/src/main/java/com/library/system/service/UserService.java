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
        if (user != null && password.equals(user.getPassword())) {
            return user;
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
        if ("1".equals(user.getRole())) {
            List<User> admins = userMapper.findByRole("1");
            if (!admins.isEmpty()) {
                throw new IllegalStateException("系统中只能有一个管理员");
            }
        }
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