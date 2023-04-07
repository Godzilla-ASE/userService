package com.example.userservice.controller;

import com.example.userservice.entity.User;
import com.example.userservice.exceptions.ResourceNotFoundException;
import com.example.userservice.repository.UserRepository;

import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@CrossOrigin("*")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final UserService userService;

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
        return userRepository.save(user);
    }

    // build get employee by id REST API
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> getUserById(@PathVariable  long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id:" + id));
        return ResponseEntity.ok(user);
    }

//    // build update employee REST API
//    @PutMapping("{id}")
//    public ResponseEntity<Employee> updateEmployee(@PathVariable long id,@RequestBody Employee employeeDetails) {
//        Employee updateEmployee = employeeRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id: " + id));
//
//        updateEmployee.setFirstName(employeeDetails.getFirstName());
//        updateEmployee.setLastName(employeeDetails.getLastName());
//        updateEmployee.setEmailId(employeeDetails.getEmailId());
//
//        employeeRepository.save(updateEmployee);
//
//        return ResponseEntity.ok(updateEmployee);
//    }

    // build delete employee REST API
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable long id){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not exist with id: " + id));

        userRepository.delete(user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<String> followUser(@PathVariable("userId") Long userId, @RequestParam("followedId") Long followedId) {
        // userId是关注者，followedId是被关注者id
        // 判断是否是自己关注自己
        if (userId.equals(followedId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot follow yourself.");
        }
        // 调用 UserService 进行关注操作
        userService.followUser(userId, followedId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{userId}/follow")
    public ResponseEntity<String> unfollowUser(@PathVariable("userId") Long userId, @RequestParam("followedId") Long followedId){
        // 判断是否已关注自己并移除粉丝和关注者
        userService.unfollowUser(userId,followedId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


}
