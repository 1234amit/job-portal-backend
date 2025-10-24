package com.example.job_portal_spring_security.auth.dto;

public class LogoutRequest {
    private String token;

    public LogoutRequest() { }
    public LogoutRequest(String token) { this.token = token; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
