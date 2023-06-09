package com.example.userservice.controller;

import com.example.userservice.dto.UserInfoDTO;
import com.example.userservice.entity.User;
import com.example.userservice.exceptions.ResourceNotFoundException;
import com.example.userservice.exceptions.UnauthorizedException;
import com.example.userservice.repository.UserRepository;

import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@CrossOrigin("*")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private RestTemplate restTemplate;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    // build create employee REST API
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        // call authServer API to generate token
        String url = "http://localhost:8081/auth/" + userId;
        String token = restTemplate.postForObject(url, null, String.class);
        // set token field in user object
        user.setToken(token);
        // set creation date
        Date now = Calendar.getInstance().getTime();
        user.setCreationDate(now);
        // save user object to database
        return userRepository.save(user);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        User savedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with username" + user.getUsername()));
        if(user.getPassword().equals(savedUser.getPassword())){
            // call authServer API to generate token
            String url = "http://auth:8081/auth/" + savedUser.getId();
            String token = restTemplate.postForObject(url, null, String.class);
            // set token field in user object
            savedUser.setToken(token);
        }else
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Incorrect username or password");


        // save user object to database
        return ResponseEntity.ok(savedUser);
    }


    // build get employee by id REST API

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> updateUserProfile(@PathVariable Long id, @RequestBody User updatedUser, @RequestHeader("Authorization") String token) {
        // call authServer API to validate token and delete it
        String url = "http://auth:8081/auth/" + id;
        String tokenAuth = restTemplate.getForObject(url,String.class);
        if(!Objects.equals(tokenAuth, token))
            throw new UnauthorizedException("Token not matched");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id: " + id));
        if (updatedUser.getUsername()!=null&&!updatedUser.getUsername().isEmpty())
            user.setUsername(updatedUser.getUsername());
        if (updatedUser.getBirthday()!=null)
            user.setBirthday(updatedUser.getBirthday());
        if (updatedUser.getEmail()!=null)
            user.setEmail(updatedUser.getEmail());
        if (updatedUser.getLocation()!=null)
            user.setLocation(updatedUser.getLocation());
        if (updatedUser.getAvatarUrl()!=null)
            user.setAvatarUrl(updatedUser.getAvatarUrl());

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // build delete employee REST API
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable long id, @RequestHeader("Authorization") String token){
        // call authServer API to validate token and delete it
        String url = "http://auth:8081/auth/" + id;
        String tokenAuth = restTemplate.getForObject(url,String.class);
        if(!Objects.equals(tokenAuth, token))
            throw new UnauthorizedException("Token not matched");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id: " + id));

        userRepository.delete(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }



    @DeleteMapping("/logout/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logoutUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id: " + id));
        // call authServer API to validate token and delete it
        String url = "http://auth:8081/auth/" + id;
        String tokenAuth = restTemplate.getForObject(url,String.class);
        if(Objects.equals(tokenAuth, token)){
            restTemplate.delete(url);
            user.setToken(null);
            userRepository.save(user);
        }
        else
            throw new UnauthorizedException("Token not matched");
    }


    @PostMapping("/{userId}/follow/{followedId}")
    public ResponseEntity<String> followUser(@PathVariable("userId") Long userId, @PathVariable("followedId") Long followedId) {
        // userId是关注者，followedId是被关注者id
        // 判断是否是自己关注自己
        if (userId.equals(followedId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot follow yourself.");
        }
        // 调用 UserService 进行关注操作
        userService.followUser(userId, followedId);
        // Notify
        String url = "http://localhost:8083/notification";
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserid_to(Math.toIntExact(followedId));
        userInfoDTO.setType("FOLLOW_USER");
        userInfoDTO.setUserid_from(Math.toIntExact(userId));
        userInfoDTO.setUserid_to(Math.toIntExact(followedId));
        restTemplate.postForObject(url, userInfoDTO, Void.class);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{userId}/follow/{followedId}")
    public ResponseEntity<String> unfollowUser(@PathVariable("userId") Long userId, @PathVariable("followedId") Long followedId){

        // 判断是否已关注自己并移除粉丝和关注者
        userService.unfollowUser(userId,followedId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> getUserById(@PathVariable  long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id:" + id));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/name/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> getUserByUsername(@PathVariable  String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with username:" + username));
        return ResponseEntity.ok(user);
    }


}
