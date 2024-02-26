package com.app.FitbitDashBoard.service;

import com.app.FitbitDashBoard.Utility.TreeNode;
import com.app.FitbitDashBoard.controller.FollowingUserInfoDTO;
import com.app.FitbitDashBoard.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    @Transactional
    User saveUser(User user);

    User getUserById(UUID id);

    List<User> getAllUsers();

    @Transactional
    User updateUser(UUID id, User updatedUser);

    @Transactional
    void deleteUser(UUID id);

    @Transactional
    void followUser(UUID followerId, UUID followedId);

    @Transactional
    void unfollowUser(UUID followerId, UUID followedId);

    Set<String> getFollowers1(UUID userId);

    Set<String> getFollowing1(UUID userId);

    @Transactional
    void followUserByEmail(String followerEmail, String followedEmail);

    @Transactional
    void unfollowUserByEmail(String followerEmail, String followedEmail);

    Set<String> getFollowingByEmail(String email);

    Set<String> getFollowersByEmail(String email);

    Set<FollowingUserInfoDTO> getFollowingUserInfoByEmail(String email) ;

    Set<FollowingUserInfoDTO> getFollowersUserInfoByEmail(String email);

    List<User> getTopUsers(int limit);

    List<TreeNode> convertToTreeNodes(List<User> users);
}
