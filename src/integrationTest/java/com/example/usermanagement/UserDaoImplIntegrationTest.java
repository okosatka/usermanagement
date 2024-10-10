package com.example.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.usermanagement.dao.UserDaoImpl;
import com.example.usermanagement.jooq.tables.records.TbUserRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

/**
 * Integration tests for {@link UserDaoImpl} using test-containers.
 */
@Testcontainers
@ActiveProfiles("test-containers")
@JooqTest
@Import(UserDaoImpl.class)
@Transactional
class UserDaoImplIntegrationTest {

    @Autowired
    private UserDaoImpl userDao;

    @Test
    void createUser_shouldPersistAndFetchUser() {
        // given
        userDao.createUser("integrationTestUser", "password", "test@example.com", "USER");

        // when
        TbUserRecord user = userDao.getUser("integrationTestUser");

        // then
        assertThat(user).isNotNull();
        assertEquals("integrationTestUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        // given
        userDao.createUser("user1", "password1", "user1@example.com", "USER");
        userDao.createUser("user2", "password2", "user2@example.com", "USER");

        // when
        List<TbUserRecord> users = userDao.getAllUsers();

        // then
        assertEquals(3, users.size()); // +1 for admin from flyway migration scripts
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        // given
        userDao.createUser("userToDelete", "password", "user@example.com", "USER");

        // when
        userDao.deleteUser("userToDelete");

        // then
        assertNull(userDao.getUser("userToDelete"));
    }

    @Test
    void updateUser_shouldUpdateUser() {
        // given
        userDao.createUser("userToUpdate", "password", "user@example.com", "USER");

        // when
        userDao.updateUser("userToUpdate", "changed", "changed@example.com", "USER");

        // then
        TbUserRecord updatedUser = userDao.getUser("userToUpdate");
        assertEquals("changed", updatedUser.getPassword());
        assertEquals("changed@example.com", updatedUser.getEmail());
        assertEquals("USER", updatedUser.getRole());
    }

    @Test
    void getUser_shouldReturnNull_whenUserDoesNotExist() {
        // when
        TbUserRecord user = userDao.getUser("nonexistent");

        // then
        assertNull(user);
    }

    @Test
    void getId_shouldReturnUsername() {
        // given
        TbUserRecord user = new TbUserRecord("integrationTestUser", "password", "test@example.com", "USER");

        // when
        String id = userDao.getId(user);

        // then
        assertEquals("integrationTestUser", id);
    }

}