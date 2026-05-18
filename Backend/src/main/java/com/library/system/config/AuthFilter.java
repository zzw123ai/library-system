package com.library.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.system.common.AuthUser;
import com.library.system.common.Result;
import com.library.system.service.AuthTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {
    public static final String AUTH_USER_ATTR = "AUTH_USER";

    private final AuthTokenService authTokenService;
    private final ObjectMapper objectMapper;

    @Value("${app.auth.enabled:true}")
    private boolean authEnabled;

    public AuthFilter(AuthTokenService authTokenService, ObjectMapper objectMapper) {
        this.authTokenService = authTokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!authEnabled) {
            return true;
        }
        String path = request.getRequestURI();
        return !path.startsWith("/api/")
                || path.startsWith("/api/user/login")
                || path.startsWith("/h2-console")
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            writeUnauthorized(response, "请先登录");
            return;
        }

        String token = authorization.substring("Bearer ".length()).trim();
        AuthUser user = authTokenService.resolveUser(token);
        if (user == null) {
            writeUnauthorized(response, "登录已失效，请重新登录");
            return;
        }

        request.setAttribute(AUTH_USER_ATTR, user);
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Result.unauthorized(message)));
    }
}
