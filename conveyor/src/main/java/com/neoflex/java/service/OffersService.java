package com.neoflex.java.service;

import com.neoflex.java.dto.CreditDTO;
import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.dto.LoanOfferDTO;
import com.neoflex.java.dto.ScoringDataDTO;

import java.util.List;

public interface OffersService {
    List<LoanOfferDTO> createPrescoringOffers(LoanApplicationRequestDTO request);

    CreditDTO createCreditOffer(ScoringDataDTO request);
}
