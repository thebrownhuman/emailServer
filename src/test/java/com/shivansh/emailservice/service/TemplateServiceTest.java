package com.shivansh.emailservice.service;

import com.shivansh.emailservice.exception.InvalidEmailTypeException;
import com.shivansh.emailservice.model.EmailType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TemplateServiceTest {

        @Autowired
        private TemplateService templateService;

        @Test
        @DisplayName("OTP template renders with all required fields")
        void renderOtpTemplate_success() {
                Map<String, Object> data = Map.of(
                                "otpCode", "482917",
                                "userName", "Shivansh",
                                "expiryMinutes", 3);

                String html = templateService.renderTemplate(EmailType.OTP, data, "AtlasID");

                assertNotNull(html);
                assertTrue(html.contains("482917"), "OTP code should appear in rendered HTML");
                assertTrue(html.contains("Shivansh"), "User name should appear in rendered HTML");
                assertTrue(html.contains("AtlasID"), "App name should appear in rendered HTML");
                assertTrue(html.contains("3"), "Expiry minutes should appear in rendered HTML");
        }

        @Test
        @DisplayName("Welcome template renders with required + optional fields")
        void renderWelcomeTemplate_success() {
                Map<String, Object> data = Map.of(
                                "userName", "Aryan",
                                "dashboardUrl", "https://atlasid.dev/dashboard");

                String html = templateService.renderTemplate(EmailType.WELCOME, data, "AtlasID");

                assertNotNull(html);
                assertTrue(html.contains("Aryan"));
                assertTrue(html.contains("https://atlasid.dev/dashboard"));
        }

        @Test
        @DisplayName("Login alert template renders with device info")
        void renderLoginAlertTemplate_success() {
                Map<String, Object> data = Map.of(
                                "userName", "Shivansh",
                                "device", "Chrome on Windows 11",
                                "location", "New Delhi, India",
                                "time", "2026-03-10 00:15 IST");

                String html = templateService.renderTemplate(EmailType.LOGIN_ALERT, data, "AtlasID");

                assertNotNull(html);
                assertTrue(html.contains("Chrome on Windows 11"));
                assertTrue(html.contains("New Delhi, India"));
        }

        @Test
        @DisplayName("Missing required fields throws InvalidEmailTypeException")
        void renderTemplate_missingFields_throwsException() {
                Map<String, Object> data = Map.of("userName", "Test");

                InvalidEmailTypeException ex = assertThrows(
                                InvalidEmailTypeException.class,
                                () -> templateService.renderTemplate(EmailType.OTP, data, "AtlasID"));

                assertTrue(ex.getMessage().contains("otpCode") || ex.getMessage().contains("expiryMinutes"),
                                "Error should mention missing required field(s)");
        }

        @Test
        @DisplayName("Generic template renders with plain text body")
        void renderGenericTemplate_success() {
                Map<String, Object> data = Map.of(
                                "userName", "Test",
                                "body", "This is a generic notification.");

                String html = templateService.renderTemplate(EmailType.GENERIC, data, "TestApp");

                assertNotNull(html);
                assertTrue(html.contains("generic notification"));
        }

        @Test
        @DisplayName("generateSubject produces correct subjects for each type")
        void generateSubject_allTypes() {
                Map<String, Object> data = Map.of("userName", "Test");

                assertEquals("AtlasID — Your verification code",
                                templateService.generateSubject(EmailType.OTP, data, "AtlasID"));
                assertEquals("Welcome to AtlasID!",
                                templateService.generateSubject(EmailType.WELCOME, data, "AtlasID"));
                assertEquals("AtlasID — New login detected",
                                templateService.generateSubject(EmailType.LOGIN_ALERT, data, "AtlasID"));
                assertEquals("AtlasID — Password changed",
                                templateService.generateSubject(EmailType.PASSWORD_CHANGED, data, "AtlasID"));
                assertEquals("AtlasID — Identity link request",
                                templateService.generateSubject(EmailType.LINK_APPROVAL, data, "AtlasID"));
                assertEquals("AtlasID — Notification",
                                templateService.generateSubject(EmailType.GENERIC, data, "AtlasID"));
        }

        @Test
        @DisplayName("All 11 email types have valid templates that render")
        void allEmailTypes_haveValidTemplates() {
                for (EmailType type : EmailType.values()) {
                        // Build the minimum required data
                        Map<String, Object> data = new java.util.HashMap<>();
                        for (String field : type.getRequiredFields()) {
                                data.put(field, "test-value");
                        }

                        assertDoesNotThrow(
                                        () -> templateService.renderTemplate(type, data, "TestApp"),
                                        "Template for " + type.name() + " should render without error");
                }
        }
}
