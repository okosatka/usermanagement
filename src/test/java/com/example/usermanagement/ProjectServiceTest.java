package com.example.usermanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.usermanagement.dao.ProjectDao;
import com.example.usermanagement.jooq.tables.records.TbUserExternalProjectRecord;
import com.example.usermanagement.service.Project;
import com.example.usermanagement.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests for {@link ProjectService} with mocked dao.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectDao projectDao;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project("projectId", "Test Project", "testOwner");
    }

    @Test
    void getAllProjects_shouldReturnListOfProjects() {
        when(projectDao.getAllProjects()).thenReturn(List.of(
                new TbUserExternalProjectRecord("projectId", "testOwner", "Test Project")
        ));

        List<Project> projects = projectService.getAllProjects();

        assertEquals(1, projects.size());
        assertEquals("Test Project", projects.get(0).name());
        verify(projectDao).getAllProjects();
    }

    @Test
    void getProjectsByOwner_shouldReturnProjectsForGivenOwner() {
        when(projectDao.getProjectsByOwner(anyString())).thenReturn(List.of(
                new TbUserExternalProjectRecord("projectId", "testOwner", "Test Project")
        ));

        List<Project> projects = projectService.getProjectsByOwner("testOwner");

        assertEquals(1, projects.size());
        assertEquals("testOwner", projects.get(0).owner());
        verify(projectDao).getProjectsByOwner("testOwner");
    }

    @Test
    void getProject_shouldReturnProjectIfExists() {
        when(projectDao.getProject(anyString(), anyString())).thenReturn(
                new TbUserExternalProjectRecord("projectId", "testOwner", "Test Project")
        );

        Optional<Project> project = projectService.getProject("projectId", "testOwner");

        assertTrue(project.isPresent());
        assertEquals("Test Project", project.get().name());
        verify(projectDao).getProject("projectId", "testOwner");
    }

    @Test
    void getProject_shouldReturnEmptyIfNotFound() {
        when(projectDao.getProject(anyString(), anyString())).thenReturn(null);

        Optional<Project> project = projectService.getProject("projectId", "unknownOwner");

        assertTrue(project.isEmpty());
        verify(projectDao).getProject("projectId", "unknownOwner");
    }

    @Test
    void createProject_shouldInvokeProjectDao() {
        projectService.createProject(testProject);

        verify(projectDao).createProject(testProject.projectId(), testProject.name(), testProject.owner());
    }
}