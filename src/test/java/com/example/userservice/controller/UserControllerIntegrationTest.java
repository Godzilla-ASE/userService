package com.example.userservice.controller;

import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @MockBean
    private TestRestTemplate restTemplate;

    @MockBean
    private UserRepository userRepository;


    @Test
    public void testCreateUser() {
        // Prepare user data
        User user = new User();
        String uniqueUsername = "user_test" + UUID.randomUUID().toString();
        user.setUsername(uniqueUsername);
        user.setPassword("pw");
        user.setEmail("testuser@example.com");

        // Mock database write
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Mock auth server response
        String token = "sample-token";
        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(token);

        // Send POST request to create a new user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(User.class)))
                .thenReturn(new ResponseEntity<>(user, HttpStatus.CREATED));

    }
}
