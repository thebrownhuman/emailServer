package com.shivansh.emailservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivansh.emailservice.model.EmailRequest;
import com.shivansh.emailservice.model.EmailResponse;
import com.shivansh.emailservice.model.EmailType;
import com.shivansh.emailservice.service.EmailSenderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "email.dev-mode=true")
class EmailControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private EmailSenderService emailSenderService;

        // Dev mode API key — bypasses auth filter
        private static final String API_KEY_HEADER = "X-API-Key";
        private static final String DEV_API_KEY = "any-key-works-in-dev";

        @Test
        @DisplayName("GET /health returns 200 with UP status (no auth needed)")
        void health_returnsUp() throws Exception {
                mockMvc.perform(get("/api/v1/email/health"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("UP"))
                                .andExpect(jsonPath("$.version").value("1.0.0"))
                                .andExpect(jsonPath("$.uptime").exists());
        }

        @Test
        @DisplayName("GET /types returns all 11 email types")
        void types_returnsAll11Types() throws Exception {
                mockMvc.perform(get("/api/v1/email/types")
                                .header(API_KEY_HEADER, DEV_API_KEY))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.types", hasSize(11)))
                                .andExpect(jsonPath("$.types[0].type").exists())
                                .andExpect(jsonPath("$.types[0].requiredFields").isArray());
        }

    @Test
    @DisplayName("POST /send with valid request returns 200")
    void sendEmail_validRequest_returns200() throws Exception {
        when(emailSenderService.sendEmail(any(EmailRequest.class)))
                .thenReturn(EmailResponse.success("test-msg-id"));

        EmailRequest request = new EmailRequest(
                "test@example.com",
                EmailType.OTP,
                "atlasid",
                "Test Subject",
                Map.of("otpCode", "123456", "userName", "Test", "expiryMinutes", 5));

        mockMvc.perform(post("/api/v1/email/send")
                .header(API_KEY_HEADER, DEV_API_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.messageId").value("test-msg-id"));
    }

        @Test
        @DisplayName("POST /send with missing 'to' field returns 400")
        void sendEmail_missingTo_returns400() throws Exception {
                String badJson = """
                                {
                                    "type": "OTP",
                                    "callerApp": "atlasid",
                                    "templateData": {"otpCode": "123456", "userName": "Test", "expiryMinutes": 5}
                                }
                                """;

                mockMvc.perform(post("/api/v1/email/send")
                                .header(API_KEY_HEADER, DEV_API_KEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(badJson))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /send with invalid email format returns 400")
        void sendEmail_invalidEmail_returns400() throws Exception {
                String badJson = """
                                {
                                    "to": "not-an-email",
                                    "type": "OTP",
                                    "callerApp": "atlasid",
                                    "templateData": {"otpCode": "123456", "userName": "Test", "expiryMinutes": 5}
                                }
                                """;

                mockMvc.perform(post("/api/v1/email/send")
                                .header(API_KEY_HEADER, DEV_API_KEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(badJson))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /send with missing callerApp returns 400")
        void sendEmail_missingCallerApp_returns400() throws Exception {
                String badJson = """
                                {
                                    "to": "test@example.com",
                                    "type": "OTP",
                                    "templateData": {"otpCode": "123456"}
                                }
                                """;

                mockMvc.perform(post("/api/v1/email/send")
                                .header(API_KEY_HEADER, DEV_API_KEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(badJson))
                                .andExpect(status().isBadRequest());
        }
}
