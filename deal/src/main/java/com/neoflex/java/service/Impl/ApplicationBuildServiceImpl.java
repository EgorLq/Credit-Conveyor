package com.neoflex.java.service.Impl;

import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.enums.ApplicationStatus;
import com.neoflex.java.enums.ChangeType;
import com.neoflex.java.model.Application;
import com.neoflex.java.model.Client;
import com.neoflex.java.model.Passport;
import com.neoflex.java.model.StatusHistoryElement;
import com.neoflex.java.service.ApplicationBuildService;
import com.neoflex.java.util.CustomLogger;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ApplicationBuildServiceImpl implements ApplicationBuildService {

    @Override
    public Client createClient(LoanApplicationRequestDTO request) {
        CustomLogger.logInfoClassAndMethod();
        Passport passport = Passport.builder()
                .series(request.getPassportSeries())
                .number(request.getPassportNumber())
                .build();

        return Client.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthdate())
                .email(request.getEmail())
                .passport(passport)
                .build();
    }

    @Override
    public Application createApplication(Client client) {
        CustomLogger.logInfoClassAndMethod();
        StatusHistoryElement statusHistoryElement = StatusHistoryElement.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .time(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .changeType(ChangeType.AUTOMATIC)
                .build();
        List<StatusHistoryElement> statusHistory = new ArrayList<>();
        statusHistory.add(statusHistoryElement);

        return Application.builder()
                .client(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .statusHistory(statusHistory)
                .build();
    }
}
