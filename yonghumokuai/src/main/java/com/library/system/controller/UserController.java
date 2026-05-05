package com.library.system.controller;

import com.library.system.entity.User;
import com.library.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin // 允许前端跨域访问
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginRequest) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (user != null) {
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", user);
        } else {
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }

    @GetMapping("/list")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/find/{id}")
    public User findById(@PathVariable Integer id) {
        return userService.findById(id);
    }

    @PostMapping("/add")
    public void insert(@RequestBody User user) {
        userService.insert(user);
    }

    @PutMapping("/update")
    public void update(@RequestBody User user) {
        userService.update(user);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }
}
