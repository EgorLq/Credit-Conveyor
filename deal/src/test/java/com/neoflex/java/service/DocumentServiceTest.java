package com.neoflex.java.service;

import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.enums.ApplicationStatus;
import com.neoflex.java.enums.ChangeType;
import com.neoflex.java.model.*;
import com.neoflex.java.service.Impl.DocumentServiceImpl;
import com.neoflex.java.service.config.BaseTestContainer;
import com.neoflex.java.kafkaConfig.KafkaProducerConfig;
import com.neoflex.java.kafkaConfig.KafkaTopicConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {DocumentServiceImpl.class})
@ComponentScan("com.neoflex.java")
@EnableAutoConfiguration
@SuppressWarnings("unused")
class DocumentServiceTest extends BaseTestContainer {
    private static final String[] ATTACHED_FILES = {"Кредитный договор.docx", "Анкета.docx", "График платежей.docx"};
    private final String documentFolder;
    @MockBean
    private KafkaService kafkaService;
    @MockBean
    private KafkaProducerConfig kafkaProducerConfig;
    @MockBean
    private KafkaTopicConfig kafkaTopicConfig;
    private final DocumentService documentService;
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

    @Autowired
    public DocumentServiceTest(@Value("${documentFolder}") String documentFolder,
                               DocumentService documentService) {
        this.documentFolder = documentFolder;
        this.documentService = documentService;

    }

    @BeforeAll
    static void prepareApplicationInstance() {
        List<StatusHistoryElement> statusHistoryElement = new ArrayList<>();
        statusHistoryElement.add(DocumentServiceTest.statusHistoryElement);
        Credit credit = Credit.builder()
                .paymentSchedule(new ArrayList<>())
                .build();
        application.setCredit(credit);
        application.setStatusHistory(statusHistoryElement);
    }

    @Test
    void createDocument() throws IOException {
        File directory = new File(documentFolder);
        FileUtils.deleteDirectory(directory);
        documentService.createDocument(application);
        for (String o : ATTACHED_FILES)
            assertTrue(new File(documentFolder + application.getId() + " " + o).exists());
        FileUtils.deleteDirectory(directory);
    }
}