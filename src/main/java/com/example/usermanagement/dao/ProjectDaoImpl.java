package com.example.usermanagement.dao;

import static com.example.usermanagement.jooq.Tables.TB_USER_EXTERNAL_PROJECT;

import com.example.usermanagement.jooq.tables.records.TbUserExternalProjectRecord;
import org.jooq.Configuration;
import org.jooq.Record2;
import org.jooq.impl.DAOImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
@Transactional
public class ProjectDaoImpl
        extends DAOImpl<TbUserExternalProjectRecord, TbUserExternalProjectRecord, Record2<String, String>>
        implements ProjectDao {

    protected ProjectDaoImpl(Configuration configuration) {
        super(TB_USER_EXTERNAL_PROJECT, TbUserExternalProjectRecord.class, configuration);
    }

    @Override
    public void createProject(String projectId, String name, String owner) {
        super.insert(new TbUserExternalProjectRecord(projectId, owner, name));
    }

    @Override
    public TbUserExternalProjectRecord getProject(String projectId, String owner) {
        return super.findById(ctx().newRecord(TB_USER_EXTERNAL_PROJECT.USERNAME, TB_USER_EXTERNAL_PROJECT.PROJECT_ID)
                .values(owner, projectId));
    }

    @Override
    public Collection<TbUserExternalProjectRecord> getAllProjects() {
        return super.findAll();
    }

    @Override
    public Collection<TbUserExternalProjectRecord> getProjectsByOwner(String owner) {
        return super.findAll().stream()
                .filter(project -> project.getUsername().equals(owner))
                .toList();
    }

    @Override
    public Record2<String, String> getId(TbUserExternalProjectRecord project) {
        return ctx().newRecord(TB_USER_EXTERNAL_PROJECT.USERNAME, TB_USER_EXTERNAL_PROJECT.PROJECT_ID)
                .values(project.getUsername(), project.getProjectId());
    }
}
