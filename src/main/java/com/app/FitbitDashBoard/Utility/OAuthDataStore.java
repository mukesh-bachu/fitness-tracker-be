package com.app.FitbitDashBoard.Utility;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OAuthDataStore {

    private final ConcurrentHashMap<String, OAuthData> store = new ConcurrentHashMap<>();

    public void saveData(String jwt, OAuthData data) {
        store.put(jwt, data);
    }

    public OAuthData getData(String jwt) {
        return store.remove(jwt); // remove to ensure single use
    }

    public static class OAuthData {
        private final String codeVerifier;
        private final String state;

        public OAuthData(String codeVerifier, String state) {
            this.codeVerifier = codeVerifier;
            this.state = state;
        }

        // Getters
        public String getCodeVerifier() {
            return codeVerifier;
        }

        public String getState() {
            return state;
        }
    }
}
