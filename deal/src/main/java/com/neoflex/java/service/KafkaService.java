package com.neoflex.java.service;

import com.neoflex.java.enums.EmailMessageTheme;
import com.neoflex.java.model.Application;

public interface KafkaService {
    void generateEmail(EmailMessageTheme emailMessageTheme, Application application);
}
