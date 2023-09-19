package com.neoflex.java.service;

import com.neoflex.java.dto.CreditDTO;
import com.neoflex.java.dto.FinishRegistrationRequestDTO;
import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.dto.LoanOfferDTO;
import com.neoflex.java.model.Application;

import java.util.List;

public interface ConveyorAccessService {
    List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO request, long id);

    CreditDTO getCreditDtoFromRemote(FinishRegistrationRequestDTO request, Application application);
}
