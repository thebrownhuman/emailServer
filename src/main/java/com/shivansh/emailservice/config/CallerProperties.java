package com.shivansh.emailservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "email")
public class CallerProperties {

    private Map<String, CallerApp> callers = new HashMap<>();

    public static class CallerApp {

        private String apiKey;
        private String displayName;
        private int rateLimit = 100;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public int getRateLimit() {
            return rateLimit;
        }

        public void setRateLimit(int rateLimit) {
            this.rateLimit = rateLimit;
        }
    }

    public Map<String, CallerApp> getCallers() {
        return callers;
    }

    public void setCallers(Map<String, CallerApp> callers) {
        this.callers = callers;
    }

    /**
     * Resolve a caller name by its API key.
     * Returns the caller name (e.g. "atlasid") or null if not found.
     */
    public String resolveCallerByApiKey(String apiKey) {
        return callers.entrySet().stream()
                .filter(e -> apiKey.equals(e.getValue().getApiKey()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the display name for a caller (used as the email From name).
     */
    public String getDisplayName(String callerName) {
        CallerApp app = callers.get(callerName);
        return app != null ? app.getDisplayName() : callerName;
    }

    /**
     * Get the rate limit (emails per hour) for a caller.
     */
    public int getRateLimit(String callerName) {
        CallerApp app = callers.get(callerName);
        return app != null ? app.getRateLimit() : 100;
    }

    /**
     * Check if a caller name + API key pair is valid.
     */
    public boolean isValidCaller(String callerName, String apiKey) {
        CallerApp app = callers.get(callerName);
        return app != null && apiKey.equals(app.getApiKey());
    }
}
