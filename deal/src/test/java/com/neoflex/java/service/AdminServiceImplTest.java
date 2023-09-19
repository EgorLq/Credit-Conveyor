package com.neoflex.java.service;

import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.enums.ApplicationStatus;
import com.neoflex.java.enums.ChangeType;
import com.neoflex.java.model.Application;
import com.neoflex.java.model.Client;
import com.neoflex.java.model.Passport;
import com.neoflex.java.model.StatusHistoryElement;
import com.neoflex.java.repository.ApplicationRepository;
import com.neoflex.java.service.Impl.AdminServiceImpl;
import com.neoflex.java.service.config.BaseTestContainer;
import com.neoflex.java.kafkaConfig.KafkaProducerConfig;
import com.neoflex.java.kafkaConfig.KafkaTopicConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {AdminServiceImpl.class})
@ComponentScan("com.neoflex.java")
@EnableAutoConfiguration
@SuppressWarnings("unused")
class AdminServiceImplTest extends BaseTestContainer {
    @MockBean
    private KafkaService kafkaService;
    @MockBean
    private KafkaProducerConfig kafkaProducerConfig;
    @MockBean
    private KafkaTopicConfig kafkaTopicConfig;

    @MockBean
    private ApplicationRepository applicationRepository;
    @Autowired
    private AdminService adminService;
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
        statusHistoryElement.add(AdminServiceImplTest.statusHistoryElement);
        application.setStatusHistory(statusHistoryElement);
    }

    @Test
    void findApplicationById() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.ofNullable(application));
        assertDoesNotThrow(() -> adminService.findApplicationById(1L));
        assertEquals(application, adminService.findApplicationById(1L));
    }

    @Test
    void findPageApplication() {
        when(applicationRepository.findAll((Pageable) any())).thenReturn(Page.empty());
        assertDoesNotThrow(adminService::findPageApplication);
        assertEquals(adminService.findPageApplication(), new ArrayList<>());
    }
}