package com.learnloop.backend.dto;

public class AuthResponse {
    private String token;
    private String email;
    private Long userId;
    private String fullName;

    public AuthResponse() {}

    public AuthResponse(String token, String email, Long userId, String fullName) {
        this.token = token;
        this.email = email;
        this.userId = userId;
        this.fullName = fullName;
    }

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }

    public void setToken(String token) { this.token = token; }
    public void setEmail(String email) { this.email = email; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
