package com.example.usermanagement.dao;

import com.example.usermanagement.jooq.tables.records.TbUserRecord;

import java.util.List;

/**
 * Data access object for the user table.
 */
public interface UserDao {

    void createUser(String name, String password, String email, String role);

    void deleteUser(String name);

    void updateUser(String name, String password, String email, String role);

    TbUserRecord getUser(String name);

    List<TbUserRecord> getAllUsers();
}
