package com.example.userservice.controller;

import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@WebMvcTest(UserController.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private UserService userService;

    @Test
    public void testCreateUser() throws Exception {
        // Prepare a sample user object
        User user = new User();
        user.setUsername("John");
        user.setPassword("1");

        // Prepare the expected response
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("John");
        savedUser.setPassword("1");
        // Mock the restTemplate.postForObject() method to return a token
        String token = "sample-token";
        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(token);
        savedUser.setToken(token);
        // Mock the userRepository.save() method to return the saved user object
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(savedUser.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(savedUser.getPassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(token));

        // Verify that the restTemplate.postForObject() method was called with the correct URL
        String expectedUrl = "http://localhost:8081/auth/" + savedUser.getId();
        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(expectedUrl, null, String.class);

        // Verify that the userRepository.save() method was called
        Mockito.verify(userRepository, Mockito.times(2)).save(Mockito.any(User.class));

        // Verify that the restTemplate.postForObject() method was called
        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(Mockito.anyString(), Mockito.isNull(), Mockito.eq(String.class));
    }
}
