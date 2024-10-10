package com.example.usermanagement;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.example.usermanagement.security.SecurityConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for {@link ProjectController} using RestAssured.
 */
@SpringBootTest(
        classes = UserManagementApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test-containers")
@Import(SecurityConfig.class)
@Transactional
class ProjectControllerIntegrationTest {

    @LocalServerPort
    public int serverPort;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // Test for GET /api/projects (accessible by all users)
    @Test
    void getAllProjects_shouldReturnProjectsList() {
        createUser("testOwner", "password", "any@test.com");
        createProject("test-project", "Test Project", "testOwner");

        RestAssuredMockMvc
                .given()
                .auth()
                .with(csrf(), SecurityMockMvcRequestPostProcessors.httpBasic("testOwner", "password"))
                .accept(ContentType.JSON)
                .when()
                .get("/api/projects")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().all()
                .body("_embedded.projectDtoList[0].projectId", org.hamcrest.Matchers.notNullValue());
    }

    // Test for GET /api/projects/owners/{owner}
    @Test
    void getProjectsByOwner_shouldReturnProjects_whenOwnerExists() {
        String owner = "testOwner";
        createUser(owner, "password", "any@test.com");
        createProject("projectId", "Test Project", "testOwner");

        RestAssuredMockMvc
                .given()
                .auth()
                .with(csrf(), SecurityMockMvcRequestPostProcessors.httpBasic("testOwner", "password"))
                .accept(ContentType.JSON)
                .when()
                .get("/api/projects/owners/{username}", owner)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().all()
                .body("_embedded.projectDtoList[0].owner", org.hamcrest.Matchers.equalTo(owner));
    }

    // Test for POST /api/projects (creating a new project)
    @Test
    void createProject_shouldReturnCreated_whenValidProject() {
        createUser("testOwner", "password", "any@test.com");

        RestAssuredMockMvc
                .given()
                .auth()
                .with(csrf(), SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password"))
                .accept(ContentType.JSON)
                .param("projectId", "test-project")
                .param("name", "Test Project")
                .param("owner", "testOwner")
                .when()
                .post("/api/projects")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .log().all();
    }

    // Test for GET /api/projects/{projectId}/ownership/{username} (get project details)
    @Test
    void getProject_shouldReturnProjectDetails_whenUserIsOwner() {
        String projectId = "test-project";
        String username = "testOwner";
        createUser(username, "password", "any@test.com");
        createProject(projectId, "Test Project", "testOwner");

        RestAssuredMockMvc
                .given()
                .auth()
                .with(csrf(), SecurityMockMvcRequestPostProcessors.httpBasic("testOwner", "password"))
                .accept(ContentType.JSON)
                .when()
                .get("/api/projects/{projectId}/ownership/{username}", projectId, username)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().all()
                .body("projectId", org.hamcrest.Matchers.equalTo(projectId))
                .body("owner", org.hamcrest.Matchers.equalTo(username));
    }

    private void createUser(String username, String password, String email) {
        RestAssuredMockMvc
                .given()
                .accept(ContentType.JSON)
                .param("username", username)
                .param("password", password)
                .param("email", email)
                .when()
                .post("/api/users")
                .then()
                .statusCode(HttpStatus.SC_CREATED);
    }

    private void createProject(String projectId, String name, String owner) {
        RestAssuredMockMvc
                .given()
                .auth()
                .with(csrf(), SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password"))
                .accept(ContentType.JSON)
                .param("projectId", projectId)
                .param("name", name)
                .param("owner", owner)
                .when()
                .post("/api/projects")
                .then()
                .statusCode(HttpStatus.SC_CREATED);
    }
}