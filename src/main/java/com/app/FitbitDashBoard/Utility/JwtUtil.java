package com.app.FitbitDashBoard.Utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Base64;
import java.util.Map;

public class JwtUtil {

    public static Map<String, Object> decodeJWT(String jwtToken) {
        // This line will decode the JWT but not verify its signature
        String withoutSignature = jwtToken.substring(0, jwtToken.lastIndexOf('.') + 1);

        // Parse the JWT Claims
        Claims claims = Jwts.parserBuilder()
                .build()
                .parseClaimsJwt(withoutSignature)
                .getBody();

        return claims;
    }

    public static Claims decodeAndVerifyJWT(String jwtToken, String secretKey) {
        // Decoding and verifying the JWT signature
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes()))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        return claims;
    }

}
