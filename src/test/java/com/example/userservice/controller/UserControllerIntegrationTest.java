package com.example.userservice.controller;

import com.example.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateUser() {
        // Prepare user data
        User user = new User();
        String uniqueUsername = "user_test" + UUID.randomUUID().toString();
        user.setUsername(uniqueUsername);
        user.setPassword("pw");
        user.setEmail("testuser@example.com");
        // ...

        // Send POST request to create a new user
        String url = "http://localhost:" + port + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.POST, request, User.class);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        User createdUser = response.getBody();
        assertNotNull(createdUser.getId());

        // Verify the token field is set
        assertNotNull(createdUser.getToken());

        // Verify the creation date is set
        assertNotNull(createdUser.getCreationDate());
    }
}
