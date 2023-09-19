package com.neoflex.java.service.Impl;

import com.neoflex.java.dto.*;
import com.neoflex.java.enums.ApplicationStatus;
import com.neoflex.java.enums.ChangeType;
import com.neoflex.java.enums.CreditStatus;
import com.neoflex.java.enums.EmailMessageTheme;
import com.neoflex.java.model.*;
import com.neoflex.java.repository.ApplicationRepository;
import com.neoflex.java.repository.ClientRepository;
import com.neoflex.java.repository.CreditRepository;
import com.neoflex.java.service.ApplicationBuildService;
import com.neoflex.java.service.ConveyorAccessService;
import com.neoflex.java.service.DealService;
import com.neoflex.java.service.DocumentService;
import com.neoflex.java.service.KafkaService;
import com.neoflex.java.mapper.CreditMapper;
import com.neoflex.java.mapper.EmploymentMapper;
import com.neoflex.java.util.CustomLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DealServiceImpl implements DealService {
    private final BigDecimal denyRate;
    private final ApplicationBuildService applicationBuildService;
    private final ConveyorAccessService conveyorAccessService;
    private final ClientRepository clientRepository;
    private final ApplicationRepository applicationRepository;
    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;
    private final EmploymentMapper employmentMapper;
    private final KafkaService kafkaService;
    private final DocumentService documentService;

    public DealServiceImpl(@Value("${denyRate}") Integer denyRate,
                           ApplicationBuildService applicationBuildService,
                           ConveyorAccessService conveyorAccessService,
                           ClientRepository clientRepository,
                           ApplicationRepository applicationRepository,
                           CreditRepository creditRepository,
                           CreditMapper creditMapper,
                           EmploymentMapper employmentMapper,
                           KafkaService kafkaService,
                           DocumentService documentService) {
        this.denyRate = BigDecimal.valueOf(denyRate);
        this.applicationBuildService = applicationBuildService;
        this.conveyorAccessService = conveyorAccessService;
        this.clientRepository = clientRepository;
        this.applicationRepository = applicationRepository;
        this.creditRepository = creditRepository;
        this.creditMapper = creditMapper;
        this.employmentMapper = employmentMapper;
        this.kafkaService = kafkaService;
        this.documentService = documentService;
    }

    @Override
    @Transactional
    public List<LoanOfferDTO> acceptRequest(LoanApplicationRequestDTO request) {
        CustomLogger.logInfoClassAndMethod();
        Client client = clientRepository.save(applicationBuildService.createClient(request));
        Application application = applicationRepository.save(applicationBuildService.createApplication(client));
        return conveyorAccessService.getOffers(request, application.getId());
    }

    @Override
    public Application updateApplication(LoanOfferDTO request) {
        CustomLogger.logInfoClassAndMethod();
        Long id = request.getApplicationId();
        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isEmpty()) return Application.builder().build();
        Application application = optionalApplication.get();
        application.setAppliedOffer(request);
        actualizeApplicationStatus(ApplicationStatus.APPROVED, application);
        kafkaService.generateEmail(EmailMessageTheme.FINISH_REGISTRATION, application);
        return applicationRepository.save(application);
    }

    @Override
    @Transactional
    public CreditDTO finishCalculateCredit(FinishRegistrationRequestDTO request, Long applicationId) {
        CustomLogger.logInfoClassAndMethod();
        Optional<Application> optionalApplication = applicationRepository.findById(applicationId);
        if (optionalApplication.isEmpty()) return CreditDTO.builder().build();
        Application application = optionalApplication.get();
        CreditDTO creditDto = conveyorAccessService.getCreditDtoFromRemote(request, application);
        actualizeClient(request, application);
        actualizeCredit(CreditStatus.CALCULATED, creditMapper.mapCredit(creditDto), application);
        deniedCheck(application);
        return creditDto;
    }

    @Override
    public void sendMessage(ApplicationStatus applicationStatus, EmailMessageTheme emailMessageTheme, Long applicationId) {
        CustomLogger.logInfoClassAndMethod();
        Optional<Application> optionalApplication = applicationRepository.findById(applicationId);
        if (optionalApplication.isEmpty()) return;
        Application application = optionalApplication.get();
        if (applicationStatus.equals(ApplicationStatus.PREPARE_DOCUMENTS)) {
            documentService.createDocument(application);
            actualizeApplicationStatus(ApplicationStatus.DOCUMENT_CREATED, application);
        }
        else actualizeApplicationStatus(applicationStatus, application);
        kafkaService.generateEmail(emailMessageTheme, application);
    }

    private void deniedCheck(Application application) {
        CustomLogger.logInfoClassAndMethod();
        if (Objects.equals(application.getCredit().getRate(), denyRate)) {
            actualizeApplicationStatus(ApplicationStatus.CC_DENIED, application);
            kafkaService.generateEmail(EmailMessageTheme.APPLICATION_DENIED, application);
        } else {
            actualizeApplicationStatus(ApplicationStatus.CC_APPROVED, application);
            kafkaService.generateEmail(EmailMessageTheme.CREATE_DOCUMENTS, application);
        }
    }

    private void actualizeApplicationStatus(ApplicationStatus applicationStatus, Application application) {
        CustomLogger.logInfoClassAndMethod();
        application.setStatus(applicationStatus);
        if (application.getStatus() == ApplicationStatus.CREDIT_ISSUED) {
            application.setSignDate(LocalDateTime.now());
            actualizeCredit(CreditStatus.ISSUED, application.getCredit(), application);
        }
        actualizeApplicationStatusHistory(applicationStatus, application);
    }

    private void actualizeApplicationStatusHistory(ApplicationStatus applicationStatus, Application application) {
        CustomLogger.logInfoClassAndMethod();
        List<StatusHistoryElement> statusHistoryElementList = application.getStatusHistory();
        StatusHistoryElement statusHistoryElement = StatusHistoryElement.builder()
                .status(applicationStatus)
                .time(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .changeType(ChangeType.AUTOMATIC)
                .build();
        statusHistoryElementList.add(statusHistoryElement);
    }

    private void actualizeClient(FinishRegistrationRequestDTO request, Application application) {
        CustomLogger.logInfoClassAndMethod();
        Client client = application.getClient();
        client.setGender(request.getGender());
        client.setMaritalStatus(request.getMaritalStatus());
        Passport passport = client.getPassport();
        passport.setIssueDate(request.getPassportIssueDate());
        passport.setIssueBranch(request.getPassportIssueBranch());
        Employment employment = employmentMapper.mapEmploymentJSON(request.getEmployment());
        client.setEmployment(employment);
        client.setAccount(request.getAccount());
        clientRepository.save(client);
    }

    private void actualizeCredit(CreditStatus creditStatus, Credit credit, Application application) {
        CustomLogger.logInfoClassAndMethod();
        credit.setCreditStatus(creditStatus);
        application.setCredit(creditRepository.save(credit));
        applicationRepository.save(application);
    }
}
