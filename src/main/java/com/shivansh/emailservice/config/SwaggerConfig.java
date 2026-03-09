package com.shivansh.emailservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Email Service API")
                        .version("1.0.0")
                        .description("Shared email microservice for the Shivansh Projects ecosystem. "
                                + "Sends templated HTML emails via Gmail SMTP on behalf of AtlasID, "
                                + "Indian Express, Shopping App, and future apps."))
                .addSecurityItem(new SecurityRequirement().addList("ApiKey"))
                .components(new Components()
                        .addSecuritySchemes("ApiKey",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-Key")
                                        .description("API key for caller app authentication. "
                                                + "Format: ems_{appName}_{randomString}")));
    }
}
