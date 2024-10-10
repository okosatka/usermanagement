package com.example.usermanagement.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.usermanagement.service.Project;
import com.example.usermanagement.service.ProjectService;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@EnableAutoConfiguration
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @WithSpan
    @GetMapping
    public CollectionModel<EntityModel<ProjectDto>> getAllProjects() {
        List<EntityModel<ProjectDto>> projects = projectService.getAllProjects().stream()
                .map(DtoMapper::toDto)
                .map(project -> EntityModel.of(
                        project,
                        linkTo(methodOn(ProjectController.class).getProject(
                                project.projectId(),
                                project.owner())).withSelfRel(),
                        linkTo(methodOn(ProjectController.class).getAllProjects()).withRel("projects")))
                .collect(Collectors.toList());

        return CollectionModel.of(
                projects,
                linkTo(methodOn(ProjectController.class).getAllProjects()).withSelfRel());
    }

    @WithSpan
    @GetMapping("/owners/{owner}")
    public CollectionModel<EntityModel<ProjectDto>> getProjectsByOwner(@PathVariable String owner) {
        List<EntityModel<ProjectDto>> projects = projectService.getProjectsByOwner(owner).stream()
                .map(DtoMapper::toDto)
                .map(project -> EntityModel.of(
                        project,
                        linkTo(methodOn(ProjectController.class).getProject(
                                project.projectId(),
                                project.owner())).withSelfRel(),
                        linkTo(methodOn(ProjectController.class).getAllProjects()).withRel("projects")))
                .collect(Collectors.toList());

        return CollectionModel.of(
                projects,
                linkTo(methodOn(ProjectController.class).getAllProjects()).withSelfRel());
    }


    @WithSpan
    @GetMapping("{projectId}/ownership/{username}")
    public ResponseEntity<EntityModel<ProjectDto>> getProject(
            @PathVariable String projectId,
            @PathVariable String username) {
        ProjectDto project = projectService.getProject(projectId, username)
                .map(DtoMapper::toDto)
                .orElseThrow(() -> new NotFoundException(String.format("Project '%s' not found", projectId)));

        EntityModel<ProjectDto> projectModel = EntityModel.of(
                project,
                linkTo(methodOn(ProjectController.class).getProject(
                        project.projectId(),
                        project.owner())).withSelfRel(),
                linkTo(methodOn(ProjectController.class).getAllProjects()).withRel("projects"));

        return ResponseEntity.ok(projectModel);
    }

    @WithSpan
    @PostMapping
    public ResponseEntity<EntityModel<ProjectDto>> createProject(
            @RequestParam String projectId,
            @RequestParam String name,
            @RequestParam String owner) {
        if (projectService.getProject(projectId, owner).isPresent()) {
            throw new AlreadyExistsException(String.format("Project '%s' already exists", owner));
        }

        Project project = new Project(projectId, name, owner);
        projectService.createProject(project);

        EntityModel<ProjectDto> projectModel = EntityModel.of(
                DtoMapper.toDto(project),
                linkTo(methodOn(ProjectController.class).getProject(
                        project.projectId(),
                        project.owner())).withSelfRel(),
                linkTo(methodOn(ProjectController.class).getAllProjects()).withRel("projects"));

        return ResponseEntity
                .created(linkTo(methodOn(ProjectController.class).getProject(
                        project.projectId(),
                        project.owner())).toUri())
                .body(projectModel);
    }

}