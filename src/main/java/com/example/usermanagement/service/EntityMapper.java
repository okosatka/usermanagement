package com.example.usermanagement.service;

import com.example.usermanagement.jooq.tables.records.TbUserExternalProjectRecord;
import com.example.usermanagement.jooq.tables.records.TbUserRecord;

/**
 * Utility class to map between domain and database entities.
 */
public class EntityMapper {
    public static User toDomain(TbUserRecord record) {
        return new User(record.getUsername(), record.getPassword(), record.getEmail(), record.getRole());
    }

    public static Project toDomain(TbUserExternalProjectRecord record) {
        return new Project(record.getProjectId(), record.getName(), record.getUsername());
    }

}