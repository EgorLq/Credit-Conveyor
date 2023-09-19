package com.neoflex.java.service;

import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.enums.ApplicationStatus;
import com.neoflex.java.enums.ChangeType;
import com.neoflex.java.model.Application;
import com.neoflex.java.model.Client;
import com.neoflex.java.model.Passport;
import com.neoflex.java.model.StatusHistoryElement;
import com.neoflex.java.service.Impl.ApplicationBuildServiceImpl;
import com.neoflex.java.service.config.BaseTestContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ApplicationBuildServiceImpl.class})
@ComponentScan("com.neoflex.java")
class ApplicationBuildServiceTest extends BaseTestContainer {
    @Autowired
    private ApplicationBuildService applicationBuildService;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(300000);
    private static final Integer TERM = 18;
    private static final LoanApplicationRequestDTO request = LoanApplicationRequestDTO.builder()
            .amount(AMOUNT).term(TERM)
            .firstName("Vasiliy")
            .lastName("Petrov")
            .email("vas@gmail.com")
            .birthdate(LocalDate.parse("1977-08-16"))
            .passportSeries("5766")
            .passportNumber("576687")
            .build();
    private static final Passport passport = Passport.builder()
            .series(request.getPassportSeries())
            .number(request.getPassportNumber())
            .build();
    private static final Client client = Client.builder()
            .lastName(request.getLastName())
            .firstName(request.getFirstName())
            .middleName(request.getMiddleName())
            .birthDate(request.getBirthdate())
            .email(request.getEmail())
            .passport(passport)
            .build();
    private static final StatusHistoryElement statusHistoryElement = StatusHistoryElement.builder()
            .status(ApplicationStatus.PREAPPROVAL)
            .time(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .changeType(ChangeType.AUTOMATIC).build();
    private static final Application application = Application.builder()
            .client(client)
            .status(ApplicationStatus.PREAPPROVAL)
            .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .build();

    @BeforeAll
    static void prepareApplicationInstance() {
        List<StatusHistoryElement> statusHistoryElement = new ArrayList<>();
        statusHistoryElement.add(ApplicationBuildServiceTest.statusHistoryElement);
        application.setStatusHistory(statusHistoryElement);
    }

    @Test
    void createClient() {
        assertEquals(client, applicationBuildService.createClient(request));
    }

    @Test
    void createApplication() {
        assertEquals(application, applicationBuildService.createApplication(client));
    }
}