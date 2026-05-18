package com.library.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.system.common.AuthUser;
import com.library.system.common.PasswordUtil;
import com.library.system.common.Result;
import com.library.system.config.AuthFilter;
import com.library.system.entity.User;
import com.library.system.service.AuthTokenService;
import com.library.system.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthTokenService authTokenService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("role", user.getRole());
            data.put("token", authTokenService.issueToken(user));
            return Result.success("登录成功", data);
        } else {
            return Result.unauthorized("用户名或密码错误");
        }
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring("Bearer ".length()).trim();
            authTokenService.revokeToken(token);
        }
        return Result.success("已退出登录");
    }

    @GetMapping("/me")
    public Result<Map<String, Object>> me(HttpServletRequest request) {
        AuthUser authUser = currentUser(request);
        if (authUser == null) {
            return Result.unauthorized("请先登录");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", authUser.getId());
        data.put("username", authUser.getUsername());
        data.put("role", authUser.getRole());
        return Result.success(data);
    }

    @GetMapping("/list")
    public Result<List<User>> findAll(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可访问");
        }
        List<User> users = userService.findAll();
        return Result.success(users);
    }

    @GetMapping("/find/{id}")
    public Result<User> findById(@PathVariable Integer id, HttpServletRequest request) {
        AuthUser authUser = currentUser(request);
        if (authUser == null) {
            return Result.unauthorized("请先登录");
        }
        if (!authUser.isAdmin() && !authUser.getId().equals(id)) {
            return Result.error(403, "无权访问其他用户信息");
        }
        User user = userService.findById(id);
        if (user != null) {
            return Result.success(user);
        }
        return Result.notFound("用户不存在");
    }

    @PostMapping("/add")
    public Result<String> insert(@RequestBody User user, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            return Result.error(400, "用户名已存在");
        }
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        userService.insert(user);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody User user, HttpServletRequest request) {
        AuthUser authUser = currentUser(request);
        if (authUser == null) {
            return Result.unauthorized("请先登录");
        }
        if (!authUser.isAdmin() && !authUser.getId().equals(user.getId())) {
            return Result.error(403, "仅可修改自己的信息");
        }
        User existingUser = userService.findById(user.getId());
        if (existingUser == null) {
            return Result.notFound("用户不存在");
        }
        // 非管理员禁止改角色
        if (!authUser.isAdmin()) {
            user.setRole(existingUser.getRole());
        }
        // 密码留空表示不修改，避免编辑用户时清空密码导致无法登录
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(existingUser.getPassword());
        } else {
            user.setPassword(PasswordUtil.encode(user.getPassword()));
        }
        userService.update(user);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可操作");
        }
        User user = userService.findById(id);
        if (user == null) {
            return Result.notFound("用户不存在");
        }
        userService.delete(id);
        return Result.success("删除成功");
    }

    @GetMapping("/search")
    public Result<List<User>> search(@RequestParam(required = false) String username,
                                     @RequestParam(required = false) String role,
                                     HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.error(403, "仅管理员可访问");
        }
        List<User> users = userService.search(username, role);
        return Result.success(users);
    }

    private AuthUser currentUser(HttpServletRequest request) {
        return (AuthUser) request.getAttribute(AuthFilter.AUTH_USER_ATTR);
    }

    private boolean isAdmin(HttpServletRequest request) {
        AuthUser user = currentUser(request);
        return user != null && user.isAdmin();
    }
}