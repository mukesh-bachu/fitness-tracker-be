package com.app.FitbitDashBoard.controller;

import com.app.FitbitDashBoard.Utility.ApiResponseWrapper;
import com.app.FitbitDashBoard.Utility.OAuthDataStore;
import com.app.FitbitDashBoard.config.JwtService;
import com.app.FitbitDashBoard.model.User;
import com.app.FitbitDashBoard.service.TokenStorageService;
import com.app.FitbitDashBoard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    @Autowired
    private UserService userService;
    private final JwtService jwtService;

    @Autowired
    private TokenStorageService tokenStorageService;


    @Value("${miracle.search}")
    private String miracleSearch;
  @PostMapping("/auth/register")
    public ResponseEntity<User> register(
            @RequestBody RegisterRequest request){
        return service.register(request);
   }


    @PostMapping("/auth/authenticate")
    public ResponseEntity<AuthenticationResponse> authentication(
            @RequestBody AuthenticationRequest request){

        return  ResponseEntity.ok(service.authenticate1(request));

    }

    @GetMapping("/search")
    public ResponseEntity<List<MiracleEmployeeSearchResposne>> searchUsers(@RequestParam String query, @RequestHeader("Authorization") String appJwt) {
        if (query == null || query.length() < 3) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }


        System.out.println("myapp jwt "+appJwt);
        // Extract user identifier (e.g., email) from the app JWT
        String jwt = appJwt.substring(7);
        String userEmail = jwtService.extractUsername(jwt);
//        String userEmail = jwtService.getEmailFromJwt(appJwt);

        System.out.println("user email "+userEmail);
        // Retrieve the corporate JWT associated with the user
//        TokenStorageService tokenStorageService = new TokenStorageService();
        String corporateJwt = tokenStorageService.getCorporateToken(jwt);

        System.out.println("corporateJwt "+corporateJwt);


        if (corporateJwt == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + corporateJwt);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        String url = miracleSearch + query;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ApiResponseWrapper> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                ApiResponseWrapper.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null && responseEntity.getBody().isSuccess()) {
            List<MiracleEmployeeSearchResposne> externalResults = responseEntity.getBody().getData();

            System.out.println(externalResults);

        Set<String> following = userService.getFollowingByEmail(userEmail); // Assuming this method exists and works correctly
            System.out.println("following "+following);

        externalResults.forEach(result -> result.setIsFollowing(following.stream().anyMatch(email -> email.equals(result.getEmail1()))));

        System.out.println(externalResults);

        return ResponseEntity.ok(externalResults);

        } else {
            // Handle the case where the API response is not successful or has no body
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }

    }




    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // or throw an exception based on your error handling strategy
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // In the case of email as username
        } else {
            return principal.toString(); // Fallback, might not be email
        }
    }


    @PostMapping("/auth/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> userDataToLogin) {
        System.out.println(userDataToLogin);
        try {
            AuthenticationResponse loginResponse = service.authenticateUser(userDataToLogin);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Login failed: " + e.getMessage()));
        }
    }



}
