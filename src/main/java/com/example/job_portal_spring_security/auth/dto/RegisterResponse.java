package com.example.job_portal_spring_security.auth.dto;

public class RegisterResponse {
    private String message;
    private String accessToken;
    private String refreshToken;

    public RegisterResponse(String message, String accessToken, String refreshToken) {
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getMessage() { return message; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }

    public void setMessage(String message) { this.message = message; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
