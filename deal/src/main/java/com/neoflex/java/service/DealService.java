package com.neoflex.java.service;

import com.neoflex.java.dto.CreditDTO;
import com.neoflex.java.dto.FinishRegistrationRequestDTO;
import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.dto.LoanOfferDTO;
import com.neoflex.java.enums.ApplicationStatus;
import com.neoflex.java.enums.EmailMessageTheme;
import com.neoflex.java.model.Application;

import java.util.List;

public interface DealService {
    List<LoanOfferDTO> acceptRequest(LoanApplicationRequestDTO request);

    Application updateApplication(LoanOfferDTO request);

    CreditDTO finishCalculateCredit(FinishRegistrationRequestDTO request, Long applicationId);

    void sendMessage(ApplicationStatus applicationStatus, EmailMessageTheme emailMessageTheme, Long applicationId);
}
