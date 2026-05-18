package com.library.system.common;

public class AuthUser {
    private final Integer id;
    private final String username;
    private final String role;

    public AuthUser(Integer id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return "1".equals(role);
    }
}
