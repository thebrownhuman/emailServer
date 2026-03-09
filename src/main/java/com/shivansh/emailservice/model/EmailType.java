package com.shivansh.emailservice.model;

import java.util.List;

public enum EmailType {

    OTP("One-time password verification",
            "otp",
            List.of("otpCode", "userName", "expiryMinutes"),
            List.of("appName")),

    WELCOME("Welcome email after registration",
            "welcome",
            List.of("userName"),
            List.of("appName", "dashboardUrl")),

    LOGIN_ALERT("New device/location login alert",
            "login-alert",
            List.of("userName", "device", "location", "time"),
            List.of("ipAddress")),

    PASSWORD_CHANGED("Password change confirmation",
            "password-changed",
            List.of("userName", "time"),
            List.of("appName")),

    LINK_APPROVAL("Identity link request received",
            "link-approval",
            List.of("userName", "serviceName", "scopes"),
            List.of("approveUrl", "expiryMinutes")),

    LINK_APPROVED("Identity link approved",
            "link-approved",
            List.of("userName", "serviceName"),
            List.of()),

    TRANSACTION_RECEIPT("Payment/transfer receipt",
            "transaction-receipt",
            List.of("userName", "amount", "merchant", "date"),
            List.of("cardLast4", "referenceId")),

    ACCOUNT_STATEMENT("Monthly account summary",
            "account-statement",
            List.of("userName", "month", "openingBalance", "closingBalance"),
            List.of("statementUrl")),

    ORDER_CONFIRMATION("Order placed confirmation",
            "order-confirmation",
            List.of("userName", "orderId", "items", "total"),
            List.of("deliveryDate", "trackingUrl")),

    SHIPPING_UPDATE("Order shipped/delivered",
            "shipping-update",
            List.of("userName", "orderId", "status"),
            List.of("trackingUrl", "deliveryDate")),

    GENERIC("Custom email (caller provides body)",
            "generic",
            List.of("userName", "body"),
            List.of("htmlBody"));

    private final String description;
    private final String templateName;
    private final List<String> requiredFields;
    private final List<String> optionalFields;

    EmailType(String description, String templateName,
            List<String> requiredFields, List<String> optionalFields) {
        this.description = description;
        this.templateName = templateName;
        this.requiredFields = requiredFields;
        this.optionalFields = optionalFields;
    }

    public String getDescription() {
        return description;
    }

    public String getTemplateName() {
        return templateName;
    }

    public List<String> getRequiredFields() {
        return requiredFields;
    }

    public List<String> getOptionalFields() {
        return optionalFields;
    }
}
