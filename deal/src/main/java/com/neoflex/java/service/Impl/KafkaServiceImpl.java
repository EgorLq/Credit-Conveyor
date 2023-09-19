package com.neoflex.java.service.Impl;

import com.neoflex.java.dto.EmailMessage;
import com.neoflex.java.enums.EmailMessageTheme;
import com.neoflex.java.model.Application;
import com.neoflex.java.service.KafkaService;
import com.neoflex.java.util.CustomLogger;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Log4j2
public class KafkaServiceImpl implements KafkaService {
    private KafkaTemplate<String, EmailMessage> kafkaTemplate;

    @Override
    public void generateEmail(EmailMessageTheme emailMessageTheme, Application application) {
        CustomLogger.logInfoClassAndMethod();
        EmailMessage emailMessage = EmailMessage.builder()
                .address(application.getClient().getEmail())
                .emailMessageTheme(emailMessageTheme)
                .applicationId(application.getId())
                .build();

        sendEmail(emailMessageTheme.toString(), emailMessage);
    }

    private void sendEmail(String topicName, EmailMessage emailMessage) {
        CustomLogger.logInfoClassAndMethod();
        CompletableFuture<SendResult<String, EmailMessage>> future = kafkaTemplate.send(topicName, emailMessage);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[" + emailMessage +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                log.info("Unable to send message=[" +
                        emailMessage + "] due to : " + ex.getMessage());
            }
        });
    }
}
