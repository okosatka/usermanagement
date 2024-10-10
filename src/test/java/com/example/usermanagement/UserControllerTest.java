package com.example.usermanagement;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.usermanagement.api.UserController;
import com.example.usermanagement.service.User;
import com.example.usermanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests for {@link UserController} with mocked service without security.
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    private static final User TEST_USER = new User("testuser", "password", "testuser@example.com", "USER");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    void getAllUsers_shouldReturnUsersList_whenAdmin() throws Exception {
        given(userService.getAllUsers()).willReturn(List.of(TEST_USER));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userDtoList[0].username").value("testuser"));
    }

    @Test
    void getUser_shouldReturnUser_whenExists() throws Exception {
        given(userService.getUser(anyString())).willReturn(Optional.of(TEST_USER));

        mockMvc.perform(get("/api/users/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }

    @Test
    @WithMockUser(username = "any", roles = "USER")
    void getUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        given(userService.getUser(anyString())).willReturn(Optional.empty());

        mockMvc.perform(get("/api/users/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_shouldCreateUser_whenValidInput() throws Exception {
        mockMvc.perform(post("/api/users")

                        .param("username", "newuser")
                        .param("password", "newpass")
                        .param("email", "newuser@example.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    void createUser_shouldReturnConflict_whenUserAlreadyExists() throws Exception {
        given(userService.userExists(anyString())).willReturn(true);

        mockMvc.perform(post("/api/users")
                        .param("username", "testuser")
                        .param("password", "newpass")
                        .param("email", "newuser@example.com"))
                .andExpect(status().isConflict());
    }

    @Test
    void updateUser_shouldUpdateUser_whenUserExists() throws Exception {
        given(userService.getUser(anyString())).willReturn(Optional.of(TEST_USER));

        mockMvc.perform(put("/api/users/testuser")
                        .param("email", "updateduser@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updateduser@example.com"));
    }

    @Test
    void updateUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        given(userService.getUser(anyString())).willReturn(Optional.empty());

        mockMvc.perform(put("/api/users/nonexistent")

                        .param("email", "updateduser@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() throws Exception {
        mockMvc.perform(delete("/api/users/testuser"))
                .andExpect(status().isNoContent());
    }
}