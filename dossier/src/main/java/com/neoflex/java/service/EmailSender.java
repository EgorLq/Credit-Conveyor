package com.neoflex.java.service;

import com.neoflex.java.dto.EmailMessage;
import com.neoflex.java.util.CustomLogger;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@PropertySource("classpath:application.yaml")
@PropertySource("classpath:private.properties")
public class EmailSender {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String addressFrom;
    private final String documentFolder;

    public EmailSender(JavaMailSender mailSender,
                       SpringTemplateEngine templateEngine,
                       @Value("${spring.mail.username}") String addressFrom,
                       @Value("${documentFolder}") String documentFolder) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.addressFrom = addressFrom;
        this.documentFolder = documentFolder;
    }

    public void send(EmailMessage emailMessage, String[] attachments) throws jakarta.mail.MessagingException {
        CustomLogger.logInfoClassAndMethod();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Map<String, Object> map = new HashMap<>();
        map.put("message", emailMessage.getEmailMessageTheme().toString());
        Context context = new Context();
        context.setVariables(map);
        String emailContent = templateEngine.process("index", context);

        mimeMessageHelper.setTo(emailMessage.getAddress());
        mimeMessageHelper.setSubject(emailMessage.getEmailMessageTheme().toString());
        mimeMessageHelper.setFrom(addressFrom);
        mimeMessageHelper.setText(emailContent, true);
        for (String o : attachments)
            mimeMessageHelper.addAttachment(o, new File(documentFolder + emailMessage.getApplicationId() + " " + o));
        mailSender.send(message);
    }
}
