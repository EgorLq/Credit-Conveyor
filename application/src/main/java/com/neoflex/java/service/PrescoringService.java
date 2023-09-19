package com.neoflex.java.service;

import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.dto.LoanOfferDTO;

import java.util.List;

public interface PrescoringService {
    List<LoanOfferDTO> getPreparedOffers(LoanApplicationRequestDTO request);

    void executeSpecifyApplicationRequest(LoanOfferDTO request);
}
