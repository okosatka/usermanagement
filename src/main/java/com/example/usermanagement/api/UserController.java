package com.example.usermanagement.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.usermanagement.security.SecurityConfig;
import com.example.usermanagement.service.User;
import com.example.usermanagement.service.UserService;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@EnableAutoConfiguration
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @WithSpan
    @GetMapping("/admin/users")
    public CollectionModel<EntityModel<UserDto>> getAllUsers() {
        List<EntityModel<UserDto>> users = userService.getAllUsers().stream()
                .map(DtoMapper::toDto)
                .map(user -> EntityModel.of(
                        user,
                        linkTo(methodOn(UserController.class).getUser(user.username())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getAllUsers()).withRel("users")))
                .collect(Collectors.toList());

        return CollectionModel.of(
                users,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }

    @WithSpan
    @GetMapping("/users/{name}")
    public ResponseEntity<EntityModel<UserDto>> getUser(@PathVariable String name) {
        UserDto user = userService.getUser(name)
                .map(DtoMapper::toDto)
                .orElseThrow(() -> new NotFoundException(String.format("User '%s' not found", name)));

        EntityModel<UserDto> userModel = EntityModel.of(
                user,
                linkTo(methodOn(UserController.class).getUser(user.username())).withSelfRel(),
                linkTo(methodOn(ProjectController.class).getProjectsByOwner(user.username())).withRel("ownership"),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));

        return ResponseEntity.ok(userModel);
    }

    @WithSpan
    @PostMapping("/users")
    public ResponseEntity<EntityModel<UserDto>> createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email) {
        if (userService.userExists(username)) {
            throw new AlreadyExistsException(String.format("User '%s' already exists", username));
        }

        User user = new User(username, password, email, SecurityConfig.ROLE_USER);
        userService.createUser(user);

        EntityModel<UserDto> userModel = EntityModel.of(
                DtoMapper.toDto(user),
                linkTo(methodOn(UserController.class).getUser(user.username())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).getUser(user.username())).toUri())
                .body(userModel);
    }

    @WithSpan
    @PutMapping("/users/{username}")
    public ResponseEntity<EntityModel<UserDto>> updateUser(
            @PathVariable String username,
            @RequestParam String email) {
        var user = userService.getUser(username)
                .orElseThrow(() -> new NotFoundException(String.format("User '%s' not found", username)));
        var updatedUser = new User(user.username(), user.password(), email, user.role());

        userService.updateUser(updatedUser);

        EntityModel<UserDto> userModel = EntityModel.of(
                DtoMapper.toDto(updatedUser),
                linkTo(methodOn(UserController.class).getUser(updatedUser.username())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));

        return ResponseEntity.ok(userModel);
    }

    @WithSpan
    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }
}