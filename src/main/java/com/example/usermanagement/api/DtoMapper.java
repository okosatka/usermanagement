package com.example.usermanagement.api;

import com.example.usermanagement.service.Project;
import com.example.usermanagement.service.User;

public class DtoMapper {
    public static UserDto toDto(User user) {
        return new UserDto(user.username(), user.email(), user.role());
    }

    public static ProjectDto toDto(Project project) {
        return new ProjectDto(project.projectId(), project.name(), project.owner());
    }

}