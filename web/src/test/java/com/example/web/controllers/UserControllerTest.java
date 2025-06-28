package com.example.web.controllers;

import com.example.web.controllers.config.BaseTestConfig;
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

    @Test
    void testRegister() {
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

        jdbcClient.sql("""
                SELECT * FROM
                book_share.user u
                JOIN book_share.user_role ur ON u.id = ur.user_id
                WHERE username = 'testuser'""").query(rs -> {
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
        RestAssured
                .given()
                .queryParam("token", "testtoken")
                .queryParam("username", "testuser")
                .contentType("application/json")
                .when()
                .get("/v1/user/confirm")
                .then()
                .statusCode(200);

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
    void testGetUserData() {
    }
}