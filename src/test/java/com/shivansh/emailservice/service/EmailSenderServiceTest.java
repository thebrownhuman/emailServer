package com.shivansh.emailservice.service;

import com.shivansh.emailservice.config.CallerProperties;
import com.shivansh.emailservice.config.RateLimitConfig;
import com.shivansh.emailservice.exception.RateLimitExceededException;
import com.shivansh.emailservice.model.EmailRequest;
import com.shivansh.emailservice.model.EmailResponse;
import com.shivansh.emailservice.model.EmailType;
import com.shivansh.emailservice.model.BatchEmailRequest;
import com.shivansh.emailservice.model.BatchEmailResponse;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

        @Mock
        private JavaMailSender mailSender;

        @Mock
        private TemplateService templateService;

        @Mock
        private CallerProperties callerProperties;

        @Mock
        private RateLimitConfig rateLimitConfig;

        @InjectMocks
        private EmailSenderService emailSenderService;

        private EmailRequest validRequest;

        @BeforeEach
        void setUp() {
                ReflectionTestUtils.setField(emailSenderService, "fromAddress", "test@gmail.com");
                ReflectionTestUtils.setField(emailSenderService, "devMode", true);

                validRequest = new EmailRequest(
                                "user@example.com",
                                EmailType.OTP,
                                "atlasid",
                                "Test OTP",
                                Map.of("otpCode", "123456", "userName", "Test", "expiryMinutes", 5));
        }

    @Test
    @DisplayName("sendEmail in dev mode logs instead of sending")
    void sendEmail_devMode_logsOnly() {
        when(rateLimitConfig.tryConsume("atlasid")).thenReturn(true);
        when(callerProperties.getDisplayName("atlasid")).thenReturn("AtlasID");
        when(templateService.renderTemplate(eq(EmailType.OTP), anyMap(), eq("AtlasID")))
                .thenReturn("<html>OTP: 123456</html>");

        EmailResponse response = emailSenderService.sendEmail(validRequest);

        assertTrue(response.isSuccess());
        assertNotNull(response.getMessageId());
        assertNotNull(response.getTimestamp());
        // Should NOT call mailSender in dev mode
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

        @Test
        @DisplayName("sendEmail auto-generates subject when not provided")
        void sendEmail_noSubject_autoGenerates() {
                EmailRequest noSubjectReq = new EmailRequest(
                                "user@example.com", EmailType.WELCOME, "atlasid", null,
                                Map.of("userName", "Test"));

                when(rateLimitConfig.tryConsume("atlasid")).thenReturn(true);
                when(callerProperties.getDisplayName("atlasid")).thenReturn("AtlasID");
                when(templateService.renderTemplate(eq(EmailType.WELCOME), anyMap(), eq("AtlasID")))
                                .thenReturn("<html>Welcome</html>");
                when(templateService.generateSubject(eq(EmailType.WELCOME), anyMap(), eq("AtlasID")))
                                .thenReturn("Welcome to AtlasID!");

                EmailResponse response = emailSenderService.sendEmail(noSubjectReq);

                assertTrue(response.isSuccess());
                verify(templateService).generateSubject(eq(EmailType.WELCOME), anyMap(), eq("AtlasID"));
        }

    @Test
    @DisplayName("sendEmail throws RateLimitExceededException when rate limited")
    void sendEmail_rateLimited_throwsException() {
        when(rateLimitConfig.tryConsume("atlasid")).thenReturn(false);
        when(rateLimitConfig.estimateRetryAfter("atlasid")).thenReturn(30L);

        assertThrows(RateLimitExceededException.class,
                () -> emailSenderService.sendEmail(validRequest));

        // Should NOT render template or send email
        verify(templateService, never()).renderTemplate(any(), anyMap(), any());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendBatch processes each email independently")
    void sendBatch_mixedResults() {
        when(rateLimitConfig.tryConsume("atlasid")).thenReturn(true);
        when(callerProperties.getDisplayName("atlasid")).thenReturn("AtlasID");
        when(templateService.renderTemplate(eq(EmailType.OTP), anyMap(), eq("AtlasID")))
                .thenReturn("<html>OTP</html>");

        BatchEmailRequest.BatchEmailItem item1 = new BatchEmailRequest.BatchEmailItem();
        item1.setTo("user1@test.com");
        item1.setType(EmailType.OTP);
        item1.setTemplateData(Map.of("otpCode", "111111", "userName", "A", "expiryMinutes", 5));

        BatchEmailRequest.BatchEmailItem item2 = new BatchEmailRequest.BatchEmailItem();
        item2.setTo("user2@test.com");
        item2.setType(EmailType.OTP);
        item2.setTemplateData(Map.of("otpCode", "222222", "userName", "B", "expiryMinutes", 5));

        BatchEmailRequest batchRequest = new BatchEmailRequest();
        batchRequest.setCallerApp("atlasid");
        batchRequest.setEmails(List.of(item1, item2));

        BatchEmailResponse response = emailSenderService.sendBatch(batchRequest);

        assertTrue(response.isSuccess());
        assertEquals(2, response.getTotal());
        assertEquals(2, response.getSent());
        assertEquals(0, response.getFailed());
    }

        @Test
        @DisplayName("sendEmail in prod mode calls doSendWithRetry")
        void sendEmail_prodMode_sendsMail() {
                ReflectionTestUtils.setField(emailSenderService, "devMode", false);

                MimeMessage mockMessage = mock(MimeMessage.class);
                when(mailSender.createMimeMessage()).thenReturn(mockMessage);
                when(rateLimitConfig.tryConsume("atlasid")).thenReturn(true);
                when(callerProperties.getDisplayName("atlasid")).thenReturn("AtlasID");
                when(templateService.renderTemplate(eq(EmailType.OTP), anyMap(), eq("AtlasID")))
                                .thenReturn("<html>OTP: 123456</html>");

                EmailResponse response = emailSenderService.sendEmail(validRequest);

                assertTrue(response.isSuccess());
                verify(mailSender).send(any(MimeMessage.class));
        }
}
