package com.example.usermanagement.service;

/**
 * User entity.
 */
public record User(String username, String password, String email, String role) {}