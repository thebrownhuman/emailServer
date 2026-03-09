package com.shivansh.emailservice.service;

import com.shivansh.emailservice.exception.InvalidEmailTypeException;
import com.shivansh.emailservice.model.EmailType;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Year;
import java.util.List;
import java.util.Map;

@Service
public class TemplateService {

    private final SpringTemplateEngine templateEngine;

    public TemplateService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Render an HTML email body for the given email type and template data.
     *
     * @param emailType    the email type (determines which template file to use)
     * @param templateData key-value pairs injected into the Thymeleaf context
     * @param displayName  the sender app display name (e.g. "AtlasID")
     * @return rendered HTML string
     * @throws InvalidEmailTypeException if required fields are missing from
     *                                   templateData
     */
    public String renderTemplate(EmailType emailType,
            Map<String, Object> templateData,
            String displayName) {

        // Validate required fields are present
        List<String> missing = emailType.getRequiredFields().stream()
                .filter(field -> !templateData.containsKey(field))
                .toList();

        if (!missing.isEmpty()) {
            throw new InvalidEmailTypeException(
                    "Missing required fields for " + emailType.name() + ": " + missing);
        }

        Context context = new Context();
        context.setVariables(templateData);
        context.setVariable("appName", displayName);
        context.setVariable("year", Year.now().getValue());

        return templateEngine.process(emailType.getTemplateName(), context);
    }

    /**
     * Auto-generate a subject line if the caller didn't provide one.
     *
     * @param emailType    the email type
     * @param templateData the template data (may contain useful info for subject)
     * @param displayName  the caller app's display name
     * @return generated subject string
     */
    public String generateSubject(EmailType emailType,
            Map<String, Object> templateData,
            String displayName) {
        return switch (emailType) {
            case OTP -> displayName + " — Your verification code";
            case WELCOME -> "Welcome to " + displayName + "!";
            case LOGIN_ALERT -> displayName + " — New login detected";
            case PASSWORD_CHANGED -> displayName + " — Password changed";
            case LINK_APPROVAL -> displayName + " — Identity link request";
            case LINK_APPROVED -> displayName + " — Identity link approved";
            case TRANSACTION_RECEIPT -> displayName + " — Transaction receipt";
            case ACCOUNT_STATEMENT -> displayName + " — Monthly statement";
            case ORDER_CONFIRMATION -> displayName + " — Order confirmed";
            case SHIPPING_UPDATE -> displayName + " — Shipping update";
            case GENERIC -> displayName + " — Notification";
        };
    }
}
