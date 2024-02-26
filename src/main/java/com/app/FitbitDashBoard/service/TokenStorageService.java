package com.app.FitbitDashBoard.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenStorageService {

    private final ConcurrentHashMap<String, String> jwtMapping = new ConcurrentHashMap<>();

    public void storeToken(String myJwt, String corporateToken) {
        jwtMapping.put(myJwt, corporateToken);
    }

    public String getCorporateToken(String myJwt) {
        return jwtMapping.get(myJwt);
    }

    // Optionally, add methods to remove tokens or handle token expiration
}
