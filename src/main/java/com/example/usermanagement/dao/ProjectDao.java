package com.example.usermanagement.dao;

import com.example.usermanagement.jooq.tables.records.TbUserExternalProjectRecord;

import java.util.Collection;

/**
 * Data access object for the project table.
 */
public interface ProjectDao {

    void createProject(String projectId, String name, String owner);

    TbUserExternalProjectRecord getProject(String projectId, String owner);

    Collection<TbUserExternalProjectRecord> getAllProjects();

    Collection<TbUserExternalProjectRecord> getProjectsByOwner(String owner);
}
