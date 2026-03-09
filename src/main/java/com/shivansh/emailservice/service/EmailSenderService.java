package com.shivansh.emailservice.service;

import com.shivansh.emailservice.config.CallerProperties;
import com.shivansh.emailservice.config.RateLimitConfig;
import com.shivansh.emailservice.exception.RateLimitExceededException;
import com.shivansh.emailservice.model.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmailSenderService {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderService.class);

    private final JavaMailSender mailSender;
    private final TemplateService templateService;
    private final CallerProperties callerProperties;
    private final RateLimitConfig rateLimitConfig;

    @Value("${email.smtp.username}")
    private String fromAddress;

    @Value("${email.dev-mode:false}")
    private boolean devMode;

    public EmailSenderService(JavaMailSender mailSender,
            TemplateService templateService,
            CallerProperties callerProperties,
            RateLimitConfig rateLimitConfig) {
        this.mailSender = mailSender;
        this.templateService = templateService;
        this.callerProperties = callerProperties;
        this.rateLimitConfig = rateLimitConfig;
    }

    /**
     * Send a single email. Checks rate limits, renders template, sends via SMTP.
     * In dev mode, logs the email to console instead of sending.
     */
    public EmailResponse sendEmail(EmailRequest request) {
        String callerName = request.getCallerApp();

        // 1. Rate limit check
        if (!rateLimitConfig.tryConsume(callerName)) {
            long retryAfter = rateLimitConfig.estimateRetryAfter(callerName);
            throw new RateLimitExceededException(retryAfter);
        }

        // 2. Resolve sender display name
        String displayName = callerProperties.getDisplayName(callerName);

        // 3. Render HTML body
        String htmlBody = templateService.renderTemplate(
                request.getType(), request.getTemplateData(), displayName);

        // 4. Resolve subject
        String subject = (request.getSubject() != null && !request.getSubject().isBlank())
                ? request.getSubject()
                : templateService.generateSubject(
                        request.getType(), request.getTemplateData(), displayName);

        // 5. Send (or log in dev mode)
        String messageId = UUID.randomUUID().toString();

        if (devMode) {
            logDevEmail(request.getTo(), displayName, subject, request.getType(), htmlBody);
        } else {
            doSendWithRetry(request.getTo(), displayName, subject, htmlBody);
        }

        return EmailResponse.success(messageId);
    }

    /**
     * Send a batch of emails. Each email is sent independently;
     * failures don't block other emails in the batch.
     */
    public BatchEmailResponse sendBatch(BatchEmailRequest request) {
        List<BatchEmailResult> results = new ArrayList<>();
        int sent = 0;
        int failed = 0;

        for (BatchEmailRequest.BatchEmailItem item : request.getEmails()) {
            try {
                EmailRequest single = new EmailRequest(
                        item.getTo(),
                        item.getType(),
                        request.getCallerApp(),
                        item.getSubject(),
                        item.getTemplateData());

                EmailResponse resp = sendEmail(single);
                results.add(new BatchEmailResult(item.getTo(), true,
                        resp.getMessageId(), null));
                sent++;
            } catch (Exception e) {
                log.error("Batch email failed for {}: {}", item.getTo(), e.getMessage());
                results.add(new BatchEmailResult(item.getTo(), false,
                        null, e.getMessage()));
                failed++;
            }
        }

        return new BatchEmailResponse(
                failed == 0, results.size(), sent, failed, results);
    }

    /**
     * Send via JavaMailSender with Spring Retry.
     * Retries up to 3 times on transient SMTP errors with exponential backoff.
     */
    @Retryable(retryFor = { MailException.class,
            MessagingException.class }, maxAttempts = 4, backoff = @Backoff(delay = 2000, multiplier = 2.5, maxDelay = 15000))
    public void doSendWithRetry(String to, String displayName,
            String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(new InternetAddress(fromAddress, displayName));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
            log.info("✅ Email sent to {} [subject={}]", to, subject);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to compose email", e);
        }
    }

    @Recover
    public void recoverFromMailFailure(MailException ex, String to,
            String displayName, String subject,
            String htmlBody) {
        log.error("❌ Failed to send email to {} after 3 retries: {}", to, ex.getMessage());
        throw new RuntimeException("SMTP_ERROR: Failed to send email to " + to, ex);
    }

    private void logDevEmail(String to, String displayName, String subject,
            EmailType type, String htmlBody) {
        log.info("═══════════════════════════════════════════════════════════");
        log.info("  📧 DEV MODE — EMAIL NOT SENT");
        log.info("  To:      {}", to);
        log.info("  From:    {} <{}>", displayName, fromAddress);
        log.info("  Subject: {}", subject);
        log.info("  Type:    {}", type);
        log.info("  Body length: {} chars", htmlBody.length());
        log.info("═══════════════════════════════════════════════════════════");
        log.debug("  HTML Body:\n{}", htmlBody);
    }
}
