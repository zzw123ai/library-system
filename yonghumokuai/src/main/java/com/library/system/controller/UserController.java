package com.library.system.controller;

import com.library.system.common.PasswordUtil;
import com.library.system.common.Result;
import com.library.system.entity.User;
import com.library.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("role", user.getRole());
            return Result.success("登录成功", data);
        } else {
            return Result.unauthorized("用户名或密码错误");
        }
    }

    @GetMapping("/list")
    public Result<List<User>> findAll() {
        List<User> users = userService.findAll();
        return Result.success(users);
    }

    @GetMapping("/find/{id}")
    public Result<User> findById(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.notFound("用户不存在");
    }

    @PostMapping("/add")
    public Result<String> insert(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return Result.error(400, "用户名已存在");
        }
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        userService.insert(user);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody User user) {
        User existingUser = userService.findById(user.getId());
        if (existingUser == null) {
            return Result.notFound("用户不存在");
        }
        userService.update(user);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        User user = userService.findById(id);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        userService.delete(id);
        return Result.success("删除成功");
    }

    @GetMapping("/search")
    public Result<List<User>> search(@RequestParam(required = false) String username, 
                                     @RequestParam(required = false) String role) {
        List<User> users = userService.search(username, role);
        return Result.success(users);
    }
}