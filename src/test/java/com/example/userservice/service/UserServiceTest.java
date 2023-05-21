package com.example.userservice.service;

import com.example.userservice.dto.UserInfoDTO;
import com.example.userservice.entity.User;
import com.example.userservice.exceptions.ResourceNotFoundException;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        restTemplate = spy(new RestTemplate());
    }

    @Test
    public void testFollowUser_Success_empty() {
        // Prepare test data
        Long userId = 1L;
        Long followedId = 2L;
        User follower = new User();
        follower.setId(userId);
        follower.setFollowings("");
        User followed = new User();
        followed.setId(followedId);
        followed.setFans("");

        // Mock UserRepository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(followedId)).thenReturn(Optional.of(followed));

         // Call the method to test
        userService.followUser(userId, followedId);

        // Verify the changes
        assertEquals(String.valueOf(followedId), follower.getFollowings());
        assertEquals(String.valueOf(userId), followed.getFans());

        // Verify the UserRepository method calls
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(followedId);
        verify(userRepository, times(1)).save(follower);
        verify(userRepository, times(1)).save(followed);
    }
    @Test
    public void testFollowUser_Success_notEmpty() {
        // Prepare test data
        Long userId = 1L;
        Long followedId = 2L;
        User follower = new User();
        follower.setId(userId);
        follower.setFollowings("3");
        User followed = new User();
        followed.setId(followedId);
        followed.setFans("4");

        // Mock UserRepository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(followedId)).thenReturn(Optional.of(followed));

        // Call the method to test
        userService.followUser(userId, followedId);

        // Verify the changes
        assertEquals("3,2", follower.getFollowings());
        assertEquals("4,1", followed.getFans());

        // Verify the UserRepository method calls
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(followedId);
        verify(userRepository, times(1)).save(follower);
        verify(userRepository, times(1)).save(followed);
    }

    @Test
    public void testFollowUser_InvalidUserId() {
        // Prepare test data
        Long userId = 1L;
        Long followedId = 2L;

        // Mock UserRepository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Call the method and verify the exception
        assertThrows(ResourceNotFoundException.class, () -> userService.followUser(userId, followedId));

        // Verify the UserRepository method calls
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findById(followedId);
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void testFollowUser_alreadyFollowed_Fail() {
        // Prepare test data
        Long userId = 1L;
        Long followedId = 2L;
        User follower = new User();
        follower.setId(userId);
        follower.setFollowings("2");
        User followed = new User();
        followed.setId(followedId);
        followed.setFans("1");
        // Mock UserRepository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(followedId)).thenReturn(Optional.of(followed));

        // Call the method and verify the exception
        assertThrows(IllegalArgumentException.class, () -> userService.followUser(userId, followedId));

        // Verify the UserRepository method calls
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(followedId);

    }

    @Test
    public void testFollowUser_alreadyFan_Fail() {
        // Prepare test data
        Long userId = 1L;
        Long followedId = 2L;
        User follower = new User();
        follower.setId(userId);
        follower.setFollowings("");
        User followed = new User();
        followed.setId(followedId);
        followed.setFans("1");
        // Mock UserRepository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(followedId)).thenReturn(Optional.of(followed));

        // Call the method and verify the exception
        assertThrows(IllegalArgumentException.class, () -> userService.followUser(userId, followedId));

        // Verify the UserRepository method calls
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(followedId);
    }

    @Test
    public void testUnfollowUser_Success() {
        // Prepare test data
        Long userId = 1L;
        Long followedId = 2L;
        User follower = new User();
        follower.setId(userId);
        follower.setFollowings("2");
        User followed = new User();
        followed.setId(followedId);
        followed.setFans("1");

        // Mock UserRepository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(follower));
        when(userRepository.findById(followedId)).thenReturn(Optional.of(followed));

        // Call the method to test
        userService.unfollowUser(userId, followedId);

        // Verify the changes
        assertEquals("", follower.getFollowings());
        assertEquals("", followed.getFans());

        // Verify the UserRepository method calls
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(followedId);
        verify(userRepository, times(1)).save(follower);
        verify(userRepository, times(1)).save(followed);
    }



}
