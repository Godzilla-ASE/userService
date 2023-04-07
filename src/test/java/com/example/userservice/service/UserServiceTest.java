package com.example.userservice.service;

import com.example.userservice.entity.User;
import com.example.userservice.exceptions.ResourceNotFoundException;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testFollowUser() {
        // 创建测试用户数据
        User follower = new User();
        follower.setUsername("follower");
        follower.setFollowings("");
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(follower));

        User followed = new User();
        followed.setUsername("followed");
        followed.setFans("");
        when(userRepository.findById(2L)).thenReturn(java.util.Optional.of(followed));

        // 获取测试用户的 ID
        Long followerId = 1L;
        Long followedId = 2L;

        // 调用 followUser 方法进行关注操作
        userService.followUser(followerId, followedId);

        // 验证关注操作是否成功
        follower = userRepository.findById(followerId).get();
        followed = userRepository.findById(followedId).get();
        List<String> followerIdList = Arrays.asList(follower.getFollowings().split(","));
        List<String> followedIdList = Arrays.asList(followed.getFans().split(","));
        assertTrue(followerIdList.contains(String.valueOf(followedId)));
        assertTrue(followedIdList.contains(String.valueOf(followerId)));
    }
}
