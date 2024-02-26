package com.app.FitbitDashBoard.controller;

import com.app.FitbitDashBoard.Utility.JwtUtil;
import com.app.FitbitDashBoard.model.User;
import com.app.FitbitDashBoard.config.JwtService;

import com.app.FitbitDashBoard.model.Role;
import com.app.FitbitDashBoard.repository.UserRepository;
import com.app.FitbitDashBoard.service.TokenStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Map;

import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Autowired
    private TokenStorageService tokenStorageService;
    @Value("${miracle.login}")
    private String loginApi;

    @Value("${miracle.encryptKey}")
    private String secretKey;

    public ResponseEntity<User> register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
//        var jwtToken = jwtService.generateToken(user);
//        var expires = jwtService.extractExpiration(jwtToken);
//        return AuthenticationResponse.builder().token(jwtToken).expires(expires).build();
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var expires = jwtService.extractExpiration(jwtToken);
        return AuthenticationResponse.builder().token(jwtToken).expires(expires).firstname(user.getFirstName()).lastname(user.getLastName()).build();
    }

    public AuthenticationResponse authenticate1(AuthenticationRequest request) {

        System.out.println(request.getFirstName());
        System.out.println(request.getLastName());
        System.out.println(request.getEmail());
        System.out.println(request);

        var user = repository.findByEmail(request.getEmail())
                .orElseGet(() ->{
                    return repository.save(User.builder()
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .email(request.getEmail())
                            .role(Role.USER)
                            .build());
                });

        var jwtToken = jwtService.generateToken(user);
        var expires = jwtService.extractExpiration(jwtToken);
        return AuthenticationResponse.builder().token(jwtToken).expires(expires).firstname(user.getFirstName()).lastname(user.getLastName()).build();
    }


    public AuthenticationResponse authenticateUser(Map<String, String> userDataToLogin) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(userDataToLogin, headers);
        System.out.println(loginApi);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(loginApi, request, Map.class);

        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            boolean success = (Boolean) responseEntity.getBody().get("success");


            if (success) {
                String corporateToken = (String) responseEntity.getBody().get("token");

              Map<String, Object> decodedToken = JwtUtil.decodeJWT(corporateToken);
                System.out.println("before corp");
//                Map<String, Object> decodedToken = JwtUtil.decodeAndVerifyJWT(corporateToken, secretKey);
                System.out.println("after corp" );
                System.out.println("decodedToken"+decodedToken);
                String firstName = (String) decodedToken.get("firstName");
                String lastName = (String) decodedToken.get("lastName");
                String email = (String) decodedToken.get("email");
                String profilePic = (String) decodedToken.get("profilePic");
                String country = (String) decodedToken.get("country");

                var user = repository.findByEmail(email)
                        .orElseGet(() ->{
                            return repository.save(User.builder()
                                    .firstName(firstName)
                                    .lastName(lastName)
                                    .email(email)
                                    .profilePic(profilePic)
                                    .country(country)
                                    .role(Role.USER)
                                    .build());
                        });

                var jwtToken = jwtService.generateToken(user);
                var expires = jwtService.extractExpiration(jwtToken);

                // Store the token using your token storage service
                tokenStorageService.storeToken(jwtToken,corporateToken);

                System.out.println("cheing the save "+tokenStorageService.getCorporateToken(jwtToken));


                return AuthenticationResponse.builder().token(jwtToken).expires(expires).firstname(user.getFirstName()).lastname(user.getLastName()).build();
            }
        }
        throw new RuntimeException("External API login failed");
    }
}
