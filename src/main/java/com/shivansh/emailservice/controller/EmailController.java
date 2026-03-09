package com.shivansh.emailservice.controller;

import com.shivansh.emailservice.model.*;
import com.shivansh.emailservice.service.EmailSenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
@Tag(name = "Email", description = "Email sending endpoints")
public class EmailController {

    private final EmailSenderService emailSenderService;
    private final Instant startupTime = Instant.now();

    @Value("${app.version:1.0.0}")
    private String appVersion;

    public EmailController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @PostMapping("/send")
    @Operation(summary = "Send a single email", description = "Send a templated email to a single recipient. "
            + "Requires a valid email type and template data with all required fields.")
    public ResponseEntity<EmailResponse> sendEmail(
            @Valid @RequestBody EmailRequest request) {
        EmailResponse response = emailSenderService.sendEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-batch")
    @Operation(summary = "Send batch emails (up to 50)", description = "Send multiple emails in a single request. "
            + "Each email is processed independently — failures don't block others.")
    public ResponseEntity<BatchEmailResponse> sendBatch(
            @Valid @RequestBody BatchEmailRequest request) {
        BatchEmailResponse response = emailSenderService.sendBatch(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    @Operation(summary = "List all supported email types", description = "Returns all email types with their required and optional template fields.")
    public ResponseEntity<Map<String, List<EmailTypeInfo>>> getTypes() {
        List<EmailTypeInfo> types = Arrays.stream(EmailType.values())
                .map(EmailTypeInfo::from)
                .toList();
        return ResponseEntity.ok(Map.of("types", types));
    }

    @GetMapping("/health")
    @Operation(summary = "Health check (no auth required)", description = "Returns service status, SMTP connectivity, uptime, and version.")
    public ResponseEntity<HealthResponse> health() {
        Duration uptime = Duration.between(startupTime, Instant.now());
        String uptimeStr = formatUptime(uptime);

        HealthResponse response = new HealthResponse(
                "UP", "connected", uptimeStr, appVersion);
        return ResponseEntity.ok(response);
    }

    private String formatUptime(Duration uptime) {
        long days = uptime.toDays();
        long hours = uptime.toHoursPart();
        long minutes = uptime.toMinutesPart();

        if (days > 0) {
            return String.format("%dd %dh %dm", days, hours, minutes);
        } else if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
}
