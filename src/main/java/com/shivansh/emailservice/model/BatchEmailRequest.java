package com.shivansh.emailservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public class BatchEmailRequest {

    @NotBlank(message = "Caller app identifier is required")
    private String callerApp;

    @NotNull(message = "Emails list is required")
    @Size(min = 1, max = 50, message = "Batch size must be between 1 and 50")
    @Valid
    private List<BatchEmailItem> emails;

    public BatchEmailRequest() {
    }

    public String getCallerApp() {
        return callerApp;
    }

    public void setCallerApp(String callerApp) {
        this.callerApp = callerApp;
    }

    public List<BatchEmailItem> getEmails() {
        return emails;
    }

    public void setEmails(List<BatchEmailItem> emails) {
        this.emails = emails;
    }

    public static class BatchEmailItem {

        @NotBlank(message = "Recipient email is required")
        @Email(message = "Invalid email format")
        private String to;

        @NotNull(message = "Email type is required")
        private EmailType type;

        private String subject;

        @NotNull(message = "Template data is required")
        private Map<String, Object> templateData;

        public BatchEmailItem() {
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public EmailType getType() {
            return type;
        }

        public void setType(EmailType type) {
            this.type = type;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public Map<String, Object> getTemplateData() {
            return templateData;
        }

        public void setTemplateData(Map<String, Object> templateData) {
            this.templateData = templateData;
        }
    }
}
