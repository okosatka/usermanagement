package com.example.usermanagement;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.usermanagement.dao.ProjectDao;
import com.example.usermanagement.dao.ProjectDaoImpl;
import com.example.usermanagement.dao.UserDao;
import com.example.usermanagement.dao.UserDaoImpl;
import com.example.usermanagement.jooq.tables.records.TbUserExternalProjectRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collection;

@Testcontainers
@ActiveProfiles("test-containers")
@JooqTest
@Import({ProjectDaoImpl.class, UserDaoImpl.class})
@Transactional
public class ProjectDaoImplIntegrationTest {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private UserDao userDao;

    @BeforeEach
    public void setup() {
        userDao.createUser("testOwner", "password", "any@test.com", "USER");
    }

    @Test
    public void testGetAllProjects() {
        // given
        projectDao.createProject("projectId", "name", "testOwner");

        // when
        Collection<TbUserExternalProjectRecord> projects = projectDao.getAllProjects();

        // them
        assertThat(projects).isNotNull();
        assertThat(projects.size()).isGreaterThan(0);
    }

    @Test
    public void testGetProjectsByOwner() {
        String owner = "testOwner";
        Collection<TbUserExternalProjectRecord> projects = projectDao.getProjectsByOwner(owner);
        assertThat(projects).isNotNull();
        assertThat(projects).allMatch(project -> project.getUsername().equals(owner));
    }

    @Test
    public void testGetProject() {
        // given
        String projectId = "testProjectId";
        String name = "name";
        String owner = "testOwner";
        projectDao.createProject(projectId, name, owner);

        // when
        TbUserExternalProjectRecord project = projectDao.getProject(projectId, owner);

        // then
        assertThat(project).isNotNull();
        assertThat(project.getProjectId()).isEqualTo(projectId);
        assertThat(project.getUsername()).isEqualTo(owner);
    }

    @Test
    public void testCreateProject() {
        String projectId = "newProjectId";
        String name = "New Project";
        String owner = "testOwner";

        projectDao.createProject(projectId, name, owner);

        TbUserExternalProjectRecord createdProject = projectDao.getProject(projectId, owner);
        assertThat(createdProject).isNotNull();
        assertThat(createdProject.getProjectId()).isEqualTo(projectId);
        assertThat(createdProject.getName()).isEqualTo(name);
        assertThat(createdProject.getUsername()).isEqualTo(owner);
    }
}