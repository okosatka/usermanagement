package com.example.usermanagement.service;

import com.example.usermanagement.dao.UserDao;
import com.example.usermanagement.security.SecurityConfig;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for user management.
 */
@Service
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @WithSpan
    @PreAuthorize("hasRole('" + SecurityConfig.ROLE_ADMIN + "')")
    public List<User> getAllUsers() {
        return userDao.getAllUsers().stream()
                .map(EntityMapper::toDomain)
                .toList();
    }

    @WithSpan
    @PreAuthorize("hasRole('" + SecurityConfig.ROLE_USER + "')")
    @PostAuthorize("returnObject.orElse(null)?.username == authentication.name or hasRole('" + SecurityConfig.ROLE_ADMIN
            + "')")
    public Optional<User> getUser(String name) {
        return Optional.ofNullable(userDao.getUser(name))
                .map(EntityMapper::toDomain);
    }

    @WithSpan
    @PreAuthorize("hasRole('" + SecurityConfig.ROLE_USER + "') and #user.username == authentication.name or hasRole('"
            + SecurityConfig.ROLE_ADMIN + "')")
    public void updateUser(User user) {
        userDao.updateUser(user.username(), passwordEncoder.encode(user.password()), user.email(), user.role());
    }

    @WithSpan
    @PreAuthorize("hasRole('" + SecurityConfig.ROLE_ADMIN + "') or #username == authentication.name")
    public void deleteUser(@P("username") String username) {
        userDao.deleteUser(username);
    }

    @WithSpan
    public void createUser(User user) {
        userDao.createUser(user.username(), passwordEncoder.encode(user.password()), user.email(), user.role());
    }

    public boolean userExists(String name) {
        return userDao.getUser(name) != null;
    }
}