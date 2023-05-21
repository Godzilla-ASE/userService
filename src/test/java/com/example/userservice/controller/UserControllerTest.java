package com.example.userservice.controller;

import com.example.userservice.dto.UserInfoDTO;
import com.example.userservice.entity.User;
import com.example.userservice.exceptions.UnauthorizedException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.NestedServletException;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private User user1;
    private User user2;
    private String token1;
    @BeforeEach
    public void setUp() {
        // Create a sample user
        user1 = new User();
        user1.setUsername("1");user1.setPassword("1");user1.setId(1L);
        token1 = "token1";user1.setToken(token1);
        user2 = new User();
        user2.setUsername("2");user2.setPassword("2");user2.setId(2L);
    }
    @Test
    public void testGetAllUsers() throws Exception {
        // Prepare a list of sample users
        List<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        // Mock the userRepository.findAll() method to return the sample user list
        when(userRepository.findAll()).thenReturn(userList);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(userList.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(userList.get(0).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value(userList.get(0).getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].password").value(userList.get(0).getPassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(userList.get(1).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value(userList.get(1).getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].password").value(userList.get(1).getPassword()));

        // Verify that the userRepository.findAll() method was called
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }
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

    @Test
    public void loginUserSuccessTest() throws Exception {
        Mockito.when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.ofNullable(user1));
        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(token1);
        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(user1)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value(user1.getPassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(token1));
        // Verify that the restTemplate.postForObject() method was called
        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(Mockito.anyString(), Mockito.isNull(), Mockito.eq(String.class));
    }
    @Test
    public void loginUserFailTest() throws Exception {
        Mockito.when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.ofNullable(user1));
        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(token1);
        User wrongUser = new User();
        wrongUser.setUsername("1");wrongUser.setId(1L);wrongUser.setPassword("wrong password");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(wrongUser)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void updateUserProfileTestWithToken() throws Exception{
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class)))
                .thenReturn(token1);
        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.ofNullable(user1));
        User userUpdated = new User();
        userUpdated.setId(1L);userUpdated.setPassword("1");
        String birthdayString = "2023-05-26";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = dateFormat.parse(birthdayString);
        userUpdated.setUsername("John");userUpdated.setBirthday(birthday);userUpdated.setEmail("uzh");
        userUpdated.setLocation("Zurich");userUpdated.setAvatarUrl("url");
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(userUpdated);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token1)
                        .content("{"
                                + "\"username\": \"John\","
                                + "\"birthday\": \"2023-05-26\","
                                + "\"email\": \"uzh\","
                                + "\"location\": \"Zurich\","
                                + "\"avatarUrl\": \"url\""
                                + "}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("2023-05-25T22:00:00.000+00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("uzh"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value("Zurich"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.avatarUrl").value("url"));

    }

    @Test
    public void testUpdateUserProfileTokenMismatch() throws Exception {
        // 模拟获取的令牌和传入的令牌不匹配
        String tokenAuth = "valid_token_from_auth_server";
        String token = "invalid_token";

        // 设置模拟的令牌验证结果
        when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(String.class)))
                .thenReturn(tokenAuth);

        // 执行PUT请求，并捕获异常
        Exception exception = assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token)
                    .content("{}"));
        });

        // 获取原始的根本异常
        Throwable rootCause = exception.getCause();

        // 验证根本异常的类型是否为UnauthorizedException
        assertTrue(rootCause instanceof UnauthorizedException);
    }

    @Test
    public void deleteUserTest() throws Exception{
        when(restTemplate.getForObject(Mockito.anyString(),Mockito.eq(String.class))).thenReturn(token1);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token1)
                .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        // Verify that the userRepository.delete() method was called
        Mockito.verify(userRepository, Mockito.times(1)).delete(Mockito.any(User.class));
    }

    @Test
    public void logoutUserTest() throws Exception{
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));
        when(restTemplate.getForObject(Mockito.anyString(),Mockito.eq(String.class))).thenReturn(token1);
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/logout/{id}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token1)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        // Verify that the userRepository.delete() method was called
        Mockito.verify(restTemplate,Mockito.times(1)).delete(Mockito.anyString());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void followUserSuccessTest() throws Exception{
        Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/users/{userId}/follow/{followedId}",1L,2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(userService).followUser(Mockito.anyLong(),Mockito.anyLong());

        // Verify the RestTemplate method calls
        verify(restTemplate, times(1)).postForObject(any(String.class), any(UserInfoDTO.class), any(Class.class));

    }

    @Test
    public void unfollowUserSuccessTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.put("/users/{userId}/follow/{followedId}",1L,2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(userService).unfollowUser(Mockito.anyLong(),Mockito.anyLong());
    }

    @Test
    public void testGetUserById_Success() throws Exception {
        // Arrange
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("john");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()));
    }

    @Test
    public void testGetUserByUsername_Success() throws Exception {
        // Arrange
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("john");
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(user));

        // Act
        mockMvc.perform(MockMvcRequestBuilders.get("/users/name/{username}","john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()));
    }

}
