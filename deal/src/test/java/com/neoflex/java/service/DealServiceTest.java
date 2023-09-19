package com.neoflex.java.service;

import com.neoflex.java.dto.*;
import com.neoflex.java.enums.*;
import com.neoflex.java.model.*;
import com.neoflex.java.repository.ApplicationRepository;
import com.neoflex.java.repository.ClientRepository;
import com.neoflex.java.service.Impl.DealServiceImpl;
import com.neoflex.java.service.config.BaseTestContainer;
import com.neoflex.java.kafkaConfig.KafkaProducerConfig;
import com.neoflex.java.kafkaConfig.KafkaTopicConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;

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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {DealServiceImpl.class})
@ComponentScan("com.neoflex.java")
@EnableAutoConfiguration
@SuppressWarnings("unused")
class DealServiceTest extends BaseTestContainer {
    @MockBean
    private ApplicationBuildService applicationBuildService;
    @MockBean
    private ConveyorAccessService conveyorAccessService;
    @MockBean
    private KafkaService kafkaService;
    @MockBean
    private KafkaTopicConfig kafkaTopicConfig;
    @MockBean
    KafkaProducerConfig kafkaProducerConfig;
    @MockBean
    private ClientRepository clientRepository;
    @MockBean
    private ApplicationRepository applicationRepository;
    @Autowired
    private DealService dealService;
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(300000);
    private static final Integer TERM = 18;
    private static final BigDecimal MONTHLY_PAYMENT = BigDecimal.valueOf(24970);
    private static final BigDecimal RATE = BigDecimal.valueOf(12.0);
    private static final BigDecimal PSK = BigDecimal.valueOf(14.978);
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

    private static final StatusHistoryElement STATUS_HISTORY = StatusHistoryElement.builder().status(ApplicationStatus.PREAPPROVAL).time(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)).changeType(ChangeType.AUTOMATIC).build();

    private static final Application application = Application.builder()
            .client(client)
            .status(ApplicationStatus.PREAPPROVAL)
            .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .build();

    private static final LoanOfferDTO loanOfferDto = LoanOfferDTO.builder()
            .applicationId(1L)
            .totalAmount(AMOUNT)
            .term(TERM)
            .monthlyPayment(MONTHLY_PAYMENT)
            .rate(RATE)
            .build();

    private static final EmploymentDTO employmentDTO = EmploymentDTO.builder()
            .employmentStatus(EmploymentStatus.EMPLOYED)
            .salary(BigDecimal.valueOf(100000))
            .position(EmploymentPosition.WORKER)
            .workExperienceTotal(120)
            .workExperienceCurrent(120)
            .build();

    @BeforeAll
    static void prepareApplicationInstance() {
        List<StatusHistoryElement> statusHistoryElement = new ArrayList<>();
        statusHistoryElement.add(STATUS_HISTORY);
        application.setStatusHistory(statusHistoryElement);
    }

    @Test
    void acceptRequest() {
        List<LoanOfferDTO> expectedList = new ArrayList<>();
        expectedList.add(loanOfferDto);
        application.setAppliedOffer(loanOfferDto);

        when(clientRepository.save(any())).thenReturn(client);
        when(applicationRepository.save(any())).thenReturn(application);
        when(conveyorAccessService.getOffers(any(LoanApplicationRequestDTO.class), anyLong())).thenReturn(expectedList);

        assertDoesNotThrow(() -> dealService.acceptRequest(request));
        LoanOfferDTO result = dealService.acceptRequest(request).get(0);

        assertEquals(1L, result.getApplicationId());
        assertEquals(AMOUNT, result.getTotalAmount());
        assertEquals(TERM, result.getTerm());
        assertEquals(RATE, result.getRate());
        assertEquals(MONTHLY_PAYMENT, result.getMonthlyPayment());
        verify(applicationBuildService, Mockito.times(2)).createClient(any());
        verify(applicationBuildService, Mockito.times(2)).createApplication(any());
        verify(conveyorAccessService, Mockito.times(2)).getOffers(any(LoanApplicationRequestDTO.class), anyLong());
    }

    @Test
    void updateApplication() {
        when(applicationRepository.save(any())).thenReturn(application);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        assertEquals(ApplicationStatus.APPROVED, dealService.updateApplication(loanOfferDto).getStatus());

        when(applicationRepository.findById(10L)).thenReturn(Optional.empty());
        loanOfferDto.setApplicationId(10L);
        assertEquals(Application.builder().build(), dealService.updateApplication(loanOfferDto));

        application.setStatus(ApplicationStatus.PREAPPROVAL);
        loanOfferDto.setApplicationId(1L);
    }

    @Test
    void calculate() {
        CreditDTO expectedCreditDTO = CreditDTO.builder()
                .amount(AMOUNT)
                .term(TERM)
                .monthlyPayment(MONTHLY_PAYMENT)
                .rate(RATE)
                .psk(PSK)
                .paymentSchedule(new ArrayList<>())
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        FinishRegistrationRequestDTO request = FinishRegistrationRequestDTO.builder()
                .employment(employmentDTO)
                .build();

        when(conveyorAccessService.getCreditDtoFromRemote(any(FinishRegistrationRequestDTO.class), any(Application.class))).thenReturn(expectedCreditDTO);

        when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
        assertEquals(expectedCreditDTO, dealService.finishCalculateCredit(request, 1L));

        when(applicationRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertEquals(dealService.finishCalculateCredit(FinishRegistrationRequestDTO.builder().build(), 1L), CreditDTO.builder().build());
    }

    @Test
    void send() {
        Credit credit = Credit.builder()
                .paymentSchedule(new ArrayList<>())
                .build();
        application.setCredit(credit);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        assertDoesNotThrow(() -> dealService.sendMessage(ApplicationStatus.PREPARE_DOCUMENTS, EmailMessageTheme.SEND_DOCUMENTS, 1L));
        assertEquals(ApplicationStatus.DOCUMENT_CREATED, application.getStatus());
    }
}