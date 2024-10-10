package com.example.usermanagement;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.usermanagement.api.ProjectController;
import com.example.usermanagement.service.Project;
import com.example.usermanagement.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests for {@link ProjectController} with mocked service without security.
 */
@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    private static final Project TEST_PROJECT = new Project("project1", "Test Project", "testuser");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    void getAllProjects_shouldReturnProjectsList() throws Exception {
        given(projectService.getAllProjects()).willReturn(List.of(TEST_PROJECT));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.projectDtoList[0].projectId").value("project1"))
                .andExpect(jsonPath("$._embedded.projectDtoList[0].name").value("Test Project"))
                .andExpect(jsonPath("$._embedded.projectDtoList[0].owner").value("testuser"));
    }

    @Test
    void getProjectsByOwner_shouldReturnProjects_whenOwnerExists() throws Exception {
        given(projectService.getProjectsByOwner(anyString())).willReturn(List.of(TEST_PROJECT));

        mockMvc.perform(get("/api/projects/owners/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.projectDtoList[0].owner").value("testuser"));
    }

    @Test
    void getProject_shouldReturnProject_whenExists() throws Exception {
        given(projectService.getProject(anyString(), anyString())).willReturn(Optional.of(TEST_PROJECT));

        mockMvc.perform(get("/api/projects/project1/ownership/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value("project1"))
                .andExpect(jsonPath("$.owner").value("testuser"));
    }

    @Test
    void getProject_shouldReturnNotFound_whenProjectDoesNotExist() throws Exception {
        given(projectService.getProject(anyString(), anyString())).willReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/nonexistent/ownership/testuser"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProject_shouldCreateProject_whenValidInput() throws Exception {
        given(projectService.getProject(anyString(), anyString())).willReturn(Optional.empty());

        mockMvc.perform(post("/api/projects")
                        .param("projectId", "newProject")
                        .param("name", "New Project")
                        .param("owner", "newuser"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectId").value("newProject"))
                .andExpect(jsonPath("$.owner").value("newuser"));
    }

    @Test
    void createProject_shouldReturnConflict_whenProjectAlreadyExists() throws Exception {
        given(projectService.getProject(anyString(), anyString())).willReturn(Optional.of(TEST_PROJECT));

        mockMvc.perform(post("/api/projects")
                        .param("projectId", "project1")
                        .param("name", "Test Project")
                        .param("owner", "testuser"))
                .andExpect(status().isConflict());
    }
}