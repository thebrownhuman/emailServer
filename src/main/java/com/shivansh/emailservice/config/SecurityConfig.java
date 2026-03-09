package com.shivansh.emailservice.config;

import com.shivansh.emailservice.filter.ApiKeyAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    private final ApiKeyAuthFilter apiKeyAuthFilter;

    public SecurityConfig(ApiKeyAuthFilter apiKeyAuthFilter) {
        this.apiKeyAuthFilter = apiKeyAuthFilter;
    }

    @Bean
    public FilterRegistrationBean<ApiKeyAuthFilter> apiKeyFilter() {
        FilterRegistrationBean<ApiKeyAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(apiKeyAuthFilter);
        registration.addUrlPatterns("/api/v1/email/*");
        registration.setOrder(1);
        return registration;
    }
}
