package com.example.usermanagement.api;

public record UserDto(String username, String email, String role) {
    public UserDto {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
    }
}
