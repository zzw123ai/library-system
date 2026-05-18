package com.library.system.config;

import java.io.IOException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.library.system.common.AuthUser;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
@Profile("test")
public class TestAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 在测试环境中，自动设置一个管理员用户
        AuthUser adminUser = new AuthUser(1, "admin", "1");
        httpRequest.setAttribute(AuthFilter.AUTH_USER_ATTR, adminUser);
        chain.doFilter(request, response);
    }
}
