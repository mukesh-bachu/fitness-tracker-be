package com.app.FitbitDashBoard.service;

import com.app.FitbitDashBoard.Utility.TreeNode;
import com.app.FitbitDashBoard.controller.FollowingUserInfoDTO;
import com.app.FitbitDashBoard.model.User;
import com.app.FitbitDashBoard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(UUID id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        // Ensure you're encrypting the password here
        existingUser.setPassword(updatedUser.getPassword());

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void followUser(UUID followerId, UUID followedId) {
        if(followerId.equals(followedId)) {
            throw new IllegalArgumentException("Users cannot follow themselves");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new NoSuchElementException("Follower not found with id: " + followerId));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new NoSuchElementException("Followed user not found with id: " + followedId));

        // Update the follower's following set
        follower.getFollowing().add(followed.getEmail());
        // Explicitly update the followed user's followers set
        followed.getFollowers().add(follower.getEmail());

        userRepository.save(follower);
        userRepository.save(followed);
    }

    @Override
    @Transactional
    public void unfollowUser(UUID followerId, UUID followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new NoSuchElementException("Follower not found with id: " + followerId));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new NoSuchElementException("Followed user not found with id: " + followedId));

        follower.getFollowing().remove(followed);
        followed.getFollowers().remove(follower);

        userRepository.save(follower);
        userRepository.save(followed);
    }

    @Override
    public Set<String> getFollowers1(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        return user.getFollowers();
    }

    @Override
    public Set<String> getFollowing1(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        return user.getFollowing();
    }



    @Override
    public Set<String> getFollowingByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + email));
        return user.getFollowing();
    }

    @Override
    public Set<String> getFollowersByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + email));
        return user.getFollowers();
    }

    @Override
    @Transactional
    public void followUserByEmail(String followerEmail, String followedEmail) {
        User follower = userRepository.findByEmail(followerEmail)
                .orElseThrow(() -> new NoSuchElementException("Follower not found with email: " + followerEmail));
        User followed = userRepository.findByEmail(followedEmail)
                .orElseThrow(() -> new NoSuchElementException("Followed user not found with email: " + followedEmail));
        System.out.println("here follow 3"+follower.getFollowing());
        follower.getFollowing().add(followed.getEmail());
        followed.getFollowers().add(follower.getEmail());
        System.out.println("here follow 4 "+follower.getFollowing());
        userRepository.save(follower);
        System.out.println("here follow 5"+follower);
        userRepository.save(followed);

        System.out.println(follower);
    }

    @Override
    @Transactional
    public void unfollowUserByEmail(String followerEmail, String followedEmail) {
        User follower = userRepository.findByEmail(followerEmail)
                .orElseThrow(() -> new NoSuchElementException("Follower not found with email: " + followerEmail));
        User followed = userRepository.findByEmail(followedEmail)
                .orElseThrow(() -> new NoSuchElementException("Followed user not found with email: " + followedEmail));

        follower.getFollowing().remove(followed.getEmail());
        followed.getFollowers().remove(follower.getEmail());
        userRepository.save(follower);
        userRepository.save(followed);
    }


    @Override
    public Set<FollowingUserInfoDTO> getFollowingUserInfoByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));

        return user.getFollowing().stream()
                .map(followedEmail -> {
                    User followedUser = userRepository.findByEmail(followedEmail)
                            .orElseThrow(() -> new NoSuchElementException("Followed user not found with email: " + followedEmail));
                    return new FollowingUserInfoDTO(followedUser.getEmail(), followedUser.getFirstName() + " "+ followedUser.getLastName(), followedUser.getSteps(), followedUser.getCalories());
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FollowingUserInfoDTO> getFollowersUserInfoByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));

        return user.getFollowers().stream()
                .map(followedEmail -> {
                    User followedUser = userRepository.findByEmail(followedEmail)
                            .orElseThrow(() -> new NoSuchElementException("Followed user not found with email: " + followedEmail));
                    return new FollowingUserInfoDTO(followedUser.getEmail(), followedUser.getFirstName() + " "+ followedUser.getLastName(), followedUser.getSteps(), followedUser.getCalories());
                })
                .collect(Collectors.toSet());
    }
    @Override
    public List<User> getTopUsers(int limit) {
        return userRepository.findTopUsers(PageRequest.of(0, limit));
    }
    @Override
    public List<TreeNode> convertToTreeNodes(List<User> users) {

        if (users.isEmpty()) return new ArrayList<>();

        Map<String, String> countryAbbreviations = new HashMap<>();
        countryAbbreviations.put("India", "in");
        countryAbbreviations.put("America", "us");


        List<TreeNode> nodes = new ArrayList<>();

        // Step 1: Convert all users to TreeNode objects initially without setting children
        for (User user : users) {
            Map<String, String> userData = new HashMap<>();
            String countryAbbreviation = countryAbbreviations.getOrDefault(user.getCountry(), "us");
            userData.put("country", countryAbbreviation); // Assuming User has a getCountry() method
            userData.put("steps", String.valueOf(user.getSteps())); // Assuming User has a getSteps() method
            nodes.add(new TreeNode(user.getFirstName(), true, userData,  new ArrayList<TreeNode>())); // Assuming User has a getName() method
        }

        // Step 2: Assign children according to a complete binary tree structure
        for (int i = 0; i < users.size(); i++) {
            TreeNode currentNode = nodes.get(i);
            int leftIndex = 2 * i + 1;
            int rightIndex = 2 * i + 2;

            if (leftIndex < users.size()) { // Check if left child exists
                currentNode.getChildren().add(nodes.get(leftIndex));
            }
            if (rightIndex < users.size()) { // Check if right child exists
                currentNode.getChildren().add(nodes.get(rightIndex));
            }
        }

        // Step 3: Return the root of the tree
        List<TreeNode> root = new ArrayList<>();
        root.add(nodes.get(0)); // Since the list is already ordered by steps, the first element is the root
        return root;
    }



}
