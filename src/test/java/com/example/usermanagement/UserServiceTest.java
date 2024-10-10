package com.example.usermanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.usermanagement.dao.UserDao;
import com.example.usermanagement.jooq.tables.records.TbUserRecord;
import com.example.usermanagement.security.SecurityConfig;
import com.example.usermanagement.service.User;
import com.example.usermanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests for {@link UserService} with mocked dao.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetAllUsers() {
        // Mocked data
        List<User> mockUsers = List.of(new User("john", "password", "john@example.com", SecurityConfig.ROLE_USER));
        when(userDao.getAllUsers()).thenReturn(List.of(
                new TbUserRecord("john", "password", "john@example.com", SecurityConfig.ROLE_USER)
        ));

        // Execute the service method
        List<User> users = userService.getAllUsers();

        // Validate results
        assertEquals(mockUsers.size(), users.size());
        assertEquals(mockUsers.get(0).username(), users.get(0).username());
        verify(userDao, times(1)).getAllUsers();
    }

    @Test
    void testGetUser() {
        // Mocked data
        User mockUser = new User("john", "password", "john@example.com", SecurityConfig.ROLE_USER);
        when(userDao.getUser("john")).thenReturn(new TbUserRecord(
                "john",
                "password",
                "john@example.com",
                SecurityConfig.ROLE_USER));

        // Execute the service method
        Optional<User> user = userService.getUser("john");

        // Validate results
        assertTrue(user.isPresent());
        assertEquals(mockUser.username(), user.get().username());
        verify(userDao, times(1)).getUser("john");
    }

    @Test
    void testUpdateUser() {
        // Mocked user input
        User user = new User("john", "newpassword", "john@example.com", SecurityConfig.ROLE_USER);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedPassword");

        // Execute the service method
        userService.updateUser(user);

        // Verify that the method calls were made
        verify(userDao, times(1)).updateUser("john", "encodedPassword", "john@example.com", SecurityConfig.ROLE_USER);
        verify(passwordEncoder, times(1)).encode("newpassword");
    }

    @Test
    void testDeleteUser() {
        // Execute the service method
        userService.deleteUser("john");

        // Verify that the method calls were made
        verify(userDao, times(1)).deleteUser("john");
    }

    @Test
    void testCreateUser() {
        // Mocked user input
        User user = new User("john", "password", "john@example.com", SecurityConfig.ROLE_USER);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Execute the service method
        userService.createUser(user);

        // Verify that the method calls were made
        verify(userDao, times(1)).createUser("john", "encodedPassword", "john@example.com", SecurityConfig.ROLE_USER);
        verify(passwordEncoder, times(1)).encode("password");
    }

    @Test
    void testUserExists() {
        // Mocked data
        when(userDao.getUser("john")).thenReturn(new TbUserRecord(
                "john",
                "password",
                "john@example.com",
                SecurityConfig.ROLE_USER));

        // Execute the service method
        boolean exists = userService.userExists("john");

        // Validate results
        assertTrue(exists);
        verify(userDao, times(1)).getUser("john");
    }
}