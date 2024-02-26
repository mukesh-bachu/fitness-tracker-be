package com.app.FitbitDashBoard.controller;

import com.app.FitbitDashBoard.Utility.PKCEUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.app.FitbitDashBoard.Utility.OAuthDataStore;

import org.springframework.web.bind.annotation.*;


import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
public class FitbitOAuthController {

    @Value("${fitbit.clientId}")
    private String clientId;

    @Value("${fitbit.clientSecret}")
    private String clientSecret;

    @Value("${fitbit.redirectUri}")
    private String redirectUri;

    @Value("${fitbit.scope}")
    private String scope;

    @Autowired
    private OAuthDataStore oauthDataStore;


    @GetMapping("/initiate-fitbit-oauth")
    public ResponseEntity<?> initiateFitbitOAuth(@RequestHeader("Authorization") String authHeader) throws NoSuchAlgorithmException {
        String jwtToken = extractJwtFromHeader(authHeader);
        String codeVerifier = PKCEUtil.generateCodeVerifier();
        String codeChallenge = PKCEUtil.generateCodeChallenge(codeVerifier);
        String state = PKCEUtil.generateState();

        oauthDataStore.saveData(jwtToken, new OAuthDataStore.OAuthData(codeVerifier, state));


        String authorizationUrl = "https://www.fitbit.com/oauth2/authorize?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=" + scope +
                "&code_challenge=" + codeChallenge +
                "&code_challenge_method=S256" +
                "&state=" + state;

        return ResponseEntity.ok(authorizationUrl);
    }

    private String extractJwtFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Extract JWT token
        }
        return null; // Or handle this as an error
    }

    @GetMapping("/fitbit")
    public ResponseEntity<?> handleFitbitCallback(@RequestParam("code") String code,
                                                  @RequestParam("state") String receivedState,
                                                  @RequestHeader("Authorization") String authHeader) {

        String jwtToken = extractJwtFromHeader(authHeader);
        OAuthDataStore.OAuthData data = oauthDataStore.getData(jwtToken);

        String codeVerifier = data.getCodeVerifier();

        String originalState = data.getState();




        if (!receivedState.equals(originalState)) {
            // Handle state mismatch - potential CSRF attack
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("State parameter mismatch");
        }

        RestTemplate restTemplate = new RestTemplate();
        String tokenEndpoint = "https://api.fitbit.com/oauth2/token";

        // Create request body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", code);
        requestBody.add("code_verifier", codeVerifier);
        requestBody.add("redirect_uri", redirectUri);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // For server applications, add Authorization header with client secret
        headers.setBasicAuth(clientId, clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        // Send POST request
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, request, Map.class);

        // Handle the response

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {

            return ResponseEntity.ok(response.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve tokens from Fitbit");
        }

        //return response;
    }
}
