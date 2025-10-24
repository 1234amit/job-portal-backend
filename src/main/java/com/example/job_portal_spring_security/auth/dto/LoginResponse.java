package com.example.job_portal_spring_security.auth.dto;

public class LoginResponse {
    private String message;
    private String accessToken;
    private UserInfo userInfo;

    public LoginResponse() {
    }

    public LoginResponse(String message, String accessToken, UserInfo userInfo) {
        this.message = message;
        this.accessToken = accessToken;
        this.userInfo = userInfo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public static class UserInfo {
        private String email;
        private String fullName;
        private Object roles;

        public UserInfo() {
        }

        public UserInfo(String email, String fullName, Object roles) {
            this.email = email;
            this.fullName = fullName;
            this.roles = roles;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public Object getRoles() {
            return roles;
        }

        public void setRoles(Object roles) {
            this.roles = roles;
        }
    }
}
