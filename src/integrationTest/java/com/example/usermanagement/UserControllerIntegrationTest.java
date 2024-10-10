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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for {@link UserController} using test-containers.
 */
@SpringBootTest(
        classes = UserManagementApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test-containers")
@Import(SecurityConfig.class)
@Transactional
class UserControllerIntegrationTest {

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
        RestAssuredMockMvc.postProcessors(csrf().asHeader());
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // Test for GET /api/admin/users (only admin can access this)
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllUsers_shouldReturnUsersList_whenAdmin() {
        RestAssuredMockMvc
                .given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/admin/users")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("_embedded.userDtoList[0].username", org.hamcrest.Matchers.equalTo("admin"));
    }

    @Test
    void createUser_shouldReturnCreated_whenValidUser() {
        RestAssuredMockMvc
                .given()
                .accept(ContentType.JSON)
                .param("username", "new-user")
                .param("password", "password")
                .param("email", "any@test.com")
                .when()
                .post("/api/users")
                .then()
                .statusCode(HttpStatus.SC_CREATED);
    }

    // Test for DELETE /api/users/{username} when user doesn't exist
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteUser_shouldReturnForbidden_whenUserDoesNotExist() {
        RestAssuredMockMvc
                .given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/users/nonexistent")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    // Test for DELETE /api/users/{username} without credentials
    @Test
    void deleteUser_shouldReturnForbidden_whenNoCredential() {
        createUser("new-user", "password", "any@test.com");

        RestAssuredMockMvc
                .given()
                .accept(ContentType.JSON)
                .when()
                .delete("/api/users/new-user")
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    // Test for Forbidden Access (role is not enough)
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getAllUsers_shouldReturnForbidden_whenNotAdmin() {
        RestAssuredMockMvc
                .given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/admin/users")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
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
}