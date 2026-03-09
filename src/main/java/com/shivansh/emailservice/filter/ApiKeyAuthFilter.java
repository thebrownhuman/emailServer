package com.shivansh.emailservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivansh.emailservice.config.CallerProperties;
import com.shivansh.emailservice.model.EmailResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);

    private final CallerProperties callerProperties;
    private final ObjectMapper objectMapper;

    @Value("${email.dev-mode:false}")
    private boolean devMode;

    public ApiKeyAuthFilter(CallerProperties callerProperties, ObjectMapper objectMapper) {
        this.callerProperties = callerProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip auth for health check and Swagger endpoints
        if (path.endsWith("/health")
                || path.contains("/swagger")
                || path.contains("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Dev mode: accept any request without API key validation
        if (devMode) {
            log.debug("Dev mode: skipping API key validation for {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-API-Key");

        if (apiKey == null || apiKey.isBlank()) {
            sendError(response, 401, "MISSING_API_KEY",
                    "X-API-Key header is required");
            return;
        }

        String caller = callerProperties.resolveCallerByApiKey(apiKey);
        if (caller == null) {
            log.warn("Invalid API key attempt: {}", apiKey.substring(0, Math.min(apiKey.length(), 10)) + "...");
            sendError(response, 401, "INVALID_API_KEY",
                    "The provided API key is not valid");
            return;
        }

        // Store resolved caller name for downstream use
        request.setAttribute("callerName", caller);
        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status,
            String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        EmailResponse errorResponse = EmailResponse.error(error, message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
