package com.example.web.controllers;

import com.example.web.controllers.config.BaseTestConfig;
import com.example.web.model.common.enums.Genre;
import com.example.web.model.user.dto.request.UserLoginRequest;
import com.example.web.model.user.dto.response.UserDataResponse;
import com.example.web.model.user.dto.response.UserLoginResponse;
import com.example.web.service.user.UserService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;

class UserControllerTest extends BaseTestConfig {

    @RegisterExtension
    private static final GreenMailExtension greenMail =
            new GreenMailExtension(ServerSetupTest.SMTP)
                    .withConfiguration(GreenMailConfiguration.aConfig()
                            .withUser("from@localhost", "test", "test"));
    @Autowired
    private JdbcClient jdbcClient;
    @Autowired
    private UserService userService;

    @Test
    void testRegister() {
        // when
        RestAssured
                .given()
                .contentType("application/json")
                .body("""
                        {
                            "username": "testuser",
                            "email": "testuser@example.com",
                            "password": "TestPassword123!"
                        }
                        """)
                .when()
                .post("/v1/user/register")
                .then()
                .statusCode(202);

        // then
        jdbcClient.sql("""
                    SELECT * FROM
                    book_share.user u
                    JOIN book_share.user_role ur ON u.id = ur.user_id
                    WHERE username = 'testuser'
                """).query(rs -> {
            // Verify user registration
            String username = rs.getString("username");
            String userId = rs.getString("id");
            String email = rs.getString("email");
            String userRole = rs.getString("role_value");
            // Use SoftAssertions to verify multiple conditions
            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(username).isEqualTo("testuser");
            softAssertions.assertThat(userId).isNotNull();
            softAssertions.assertThat(email).isEqualTo("testuser@example.com");
            softAssertions.assertThat(userRole).isEqualTo("User");
            //  Verify email was sent
            softAssertions.assertThat(greenMail.getReceivedMessages().length)
                    .as("Email should be sent")
                    .isGreaterThan(0);

            softAssertions.assertAll();
        });
    }

    @Test
    @Sql(scripts = "/db/testLogin.sql")
    void testLoginUser() {
        // when
        RestAssured
                .given()
                .contentType("application/json")
                .body("""
                        {
                            "username": "testuser",
                            "password": "dupa123"
                        }
                        """)
                .when()
                .post("/v1/user/login")
                .then()
                .statusCode(200)
                .body("token", org.hamcrest.Matchers.notNullValue());

        // then
        // Verify that the user is logged in by checking the database
        jdbcClient.sql("""
                SELECT * FROM book_share.user WHERE username = 'testuser'
                """).query(rs -> {
            String username = rs.getString("username");
            boolean enabled = rs.getBoolean("enabled");
            // Assert that the user is enabled
            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(username).isEqualTo("testuser");
            softAssertions.assertThat(enabled).isTrue();
            softAssertions.assertAll();
        });
    }

    @Test
    @Sql(scripts = "/db/testConfirmUser.sql")
    void testConfirmRegistration() {
        // when
        RestAssured
                .given()
                .queryParam("token", "testtoken")
                .queryParam("username", "testuser")
                .contentType("application/json")
                .when()
                .get("/v1/user/confirm")
                .then()
                .statusCode(200);

        // then
        // Verify that the user is confirmed in the database
        jdbcClient.sql("""
                SELECT * FROM book_share.user WHERE username = 'testuser'
                """).query(rs -> {
            boolean enabled = rs.getBoolean("enabled");
            SoftAssertions softAssertions = new SoftAssertions();
            softAssertions.assertThat(enabled).isTrue();
            softAssertions.assertAll();
        });
    }

    @Test
    @Sql(scripts = "/db/testUserData.sql")
    void testGetUserData() {
        // given
        // Authenticate the user to get a valid token
        UserLoginResponse authenticateUser = userService.authenticateUser(new UserLoginRequest(
                "testuser",
                "dupa123"
        ));

        // when
        UserDataResponse userDataResponse = RestAssured
                .given()
                .auth()
                .oauth2(authenticateUser.token())
                .when()
                .get("/v1/user/data")
                .then()
                .statusCode(200)
                .extract()
                .as(UserDataResponse.class);

        // then
        // Verify the user data response
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(userDataResponse.username()).isEqualTo("testuser");
        softAssertions.assertThat(userDataResponse.email()).isEqualTo("yrdy@gmail.com");
        softAssertions.assertThat(userDataResponse.userBooks()).hasSize(1);
        softAssertions.assertThat(userDataResponse.userBooks().getFirst().title()).isEqualTo("Test Book");
        softAssertions.assertThat(userDataResponse.userBooks().getFirst().author()).isEqualTo("Test Author");
        softAssertions.assertThat(userDataResponse.userBooks().getFirst().genre()).isEqualTo(Genre.FICTION);
        softAssertions.assertThat(userDataResponse.userBooks().getFirst().imageUrl()).isEqualTo("http://example.com/image.jpg");
        softAssertions.assertThat(userDataResponse.userBooks().getFirst().description()).isEqualTo("This is a test book description.");
        softAssertions.assertThat(userDataResponse.likedBooks()).hasSize(1);
        softAssertions.assertThat(userDataResponse.likedBooks().getFirst().title()).isEqualTo("Test Book");
        softAssertions.assertThat(userDataResponse.readBooks()).hasSize(1);
        softAssertions.assertThat(userDataResponse.readBooks().getFirst().title()).isEqualTo("Test Book");
        softAssertions.assertThat(userDataResponse.readBooks().getFirst().author()).isEqualTo("Test Author");
        softAssertions.assertThat(userDataResponse.readBooks().getFirst().genre()).isEqualTo(Genre.FICTION);
        softAssertions.assertThat(userDataResponse.readBooks().getFirst().imageUrl()).isEqualTo("http://example.com/image.jpg");
        softAssertions.assertThat(userDataResponse.readBooks().getFirst().description()).isEqualTo("This is a test book description.");
        softAssertions.assertThat(userDataResponse.borrowedBooks()).isEmpty();
        softAssertions.assertAll();
    }
}