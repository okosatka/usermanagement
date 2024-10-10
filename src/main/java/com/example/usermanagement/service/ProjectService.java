package com.example.usermanagement.service;

import static com.example.usermanagement.security.SecurityConfig.ROLE_ADMIN;

import com.example.usermanagement.dao.ProjectDao;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for project management.
 */
@Service
public class ProjectService {

    private final ProjectDao projectDao;

    public ProjectService(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    @WithSpan
    public List<Project> getAllProjects() {
        return projectDao.getAllProjects().stream()
                .map(EntityMapper::toDomain)
                .toList();
    }

    @WithSpan
    public List<Project> getProjectsByOwner(String username) {
        return projectDao.getProjectsByOwner(username).stream()
                .map(EntityMapper::toDomain)
                .toList();
    }

    @WithSpan
    public Optional<Project> getProject(String projectId, String owner) {
        return Optional.ofNullable(projectDao.getProject(projectId, owner)).map(EntityMapper::toDomain);
    }

    @WithSpan
    @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    public void createProject(Project project) {
        projectDao.createProject(project.projectId(), project.name(), project.owner());
    }
}
