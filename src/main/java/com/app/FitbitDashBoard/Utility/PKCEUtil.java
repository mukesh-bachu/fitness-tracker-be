package com.app.FitbitDashBoard.Utility;

import org.springframework.beans.factory.annotation.Value;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class PKCEUtil {

    //@Value("${fitbit.CHARACTERS}")
    private static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~";
    //@Value("${fitbit.VERIFIER_LENGTH}")
    private static int VERIFIER_LENGTH =64;

    public static String generateCodeVerifier() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(VERIFIER_LENGTH);
        for (int i = 0; i < VERIFIER_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(r.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(codeVerifier.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(md.digest());
    }



    public static String generateState() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16); // Length can be adjusted
        for (int i = 0; i < 16; i++) {
            sb.append(CHARACTERS.charAt(r.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
