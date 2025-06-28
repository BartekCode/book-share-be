package com.example.core.services.email;

import com.example.core.model.email.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    private String email;
    private JavaMailSender javaMailSender;
    private SpringTemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, @Value("${mail.from}") String email) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.email = email;
    }

    public EmailService() {
    }

    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplateName,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        String templateName;
        if (emailTemplateName == null) {
            templateName = "confirm_email";
        } else {
            templateName = emailTemplateName.getTemplateName();
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        // property do szablonu html
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        mimeMessageHelper.setFrom(email);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);

        String template = templateEngine.process(templateName, context);

        mimeMessageHelper.setText(template, true);

        javaMailSender.send(mimeMessage);
    }
}
