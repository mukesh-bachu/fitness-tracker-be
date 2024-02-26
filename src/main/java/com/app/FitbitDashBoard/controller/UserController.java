package com.app.FitbitDashBoard.controller;

import com.app.FitbitDashBoard.Utility.TreeNode;
import com.app.FitbitDashBoard.exception.UserNotFoundException;
import com.app.FitbitDashBoard.model.User;
import com.app.FitbitDashBoard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
//
//    @PostMapping
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        User createdUser = userService.saveUser(user);
//        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(id, updatedUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{email}/following")
    public ResponseEntity<Set<FollowingUserInfoDTO>> getFollowing(@PathVariable String email) {
        try {
            Set<FollowingUserInfoDTO> followingInfo = userService.getFollowingUserInfoByEmail(email);
            if(followingInfo.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(followingInfo, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // Endpoint to get the list of users following the specified user
    @GetMapping("/{email}/followers")
    public ResponseEntity<Set<FollowingUserInfoDTO>> getFollowers(@PathVariable String email) {
        try {
            Set<FollowingUserInfoDTO> followers = userService.getFollowersUserInfoByEmail(email);
            if(followers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/top7")
    public ResponseEntity<List<TreeNode>> getTop7Users() {
        try {
            List<User> topUsers = userService.getTopUsers(7);
            return new ResponseEntity<>(userService.convertToTreeNodes(topUsers), HttpStatus.OK);
        } catch(UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Endpoint to allow a user to follow another user by email
    @PostMapping("/{followerEmail}/follow/{followedEmail}")
    public ResponseEntity<?> followUserByEmail(@PathVariable String followerEmail, @PathVariable String followedEmail) {
        System.out.println("here to follow 2");
        try {
            if (followerEmail.equals(followedEmail)) {
                return new ResponseEntity<>("User cannot follow themselves.", HttpStatus.BAD_REQUEST);
            }
            System.out.println("here to follow");
            userService.followUserByEmail(followerEmail, followedEmail);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            System.out.println("error here 1" + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.out.println("error here 2" + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to allow a user to unfollow another user by email
    @PostMapping("/{followerEmail}/unfollow/{followedEmail}")
    public ResponseEntity<?> unfollowUserByEmail(@PathVariable String followerEmail, @PathVariable String followedEmail) {
        try {
            userService.unfollowUserByEmail(followerEmail, followedEmail);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Exception handling
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

        }
