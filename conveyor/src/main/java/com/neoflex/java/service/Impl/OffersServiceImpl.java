package com.neoflex.java.service.Impl;

import com.neoflex.java.dto.*;
import com.neoflex.java.service.OffersService;
import com.neoflex.java.service.ScoringService;
import com.neoflex.java.util.CustomLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class OffersServiceImpl implements OffersService {

    private final ScoringService scoringService;
    private final Integer denyRate;

    @Autowired
    public OffersServiceImpl(ScoringService scoringService,
                             @Value("${denyRate}") Integer denyRate) {
        this.scoringService = scoringService;
        this.denyRate = denyRate;
    }

    @Override
    public List<LoanOfferDTO> createPrescoringOffers(LoanApplicationRequestDTO request) {
        CustomLogger.logInfoClassAndMethod();
        return Stream.of(
                createPrescoringOffer(true, true, request),
                createPrescoringOffer(true, false, request),
                createPrescoringOffer(false, true, request),
                createPrescoringOffer(false, false, request)
        ).sorted(Comparator.comparing(LoanOfferDTO::getRate).reversed()).toList();
    }

    private LoanOfferDTO createPrescoringOffer(boolean isInsuranceEnabled,
                                               boolean isSalaryClient,
                                               LoanApplicationRequestDTO request) {
        CustomLogger.logInfoClassAndMethod();
        BigDecimal amount = request.getAmount();
        Integer term = request.getTerm();

        BigDecimal rate = scoringService.calculatePrescoringRate(isInsuranceEnabled, isSalaryClient);
        BigDecimal totalAmount = scoringService.evaluateTotalAmount(amount, isInsuranceEnabled);
        BigDecimal monthlyPayment = scoringService.calculateMonthlyPayment(amount, term, rate);

        return LoanOfferDTO.builder()
                .requestedAmount(amount)
                .totalAmount(totalAmount)
                .term(term)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .rate(rate)
                .monthlyPayment(monthlyPayment)
                .build();
    }

    @Override
    public CreditDTO createCreditOffer(ScoringDataDTO scoringDataDTO) {
        CustomLogger.logInfoClassAndMethod();
        BigDecimal amount = scoringDataDTO.getAmount();
        Integer term = scoringDataDTO.getTerm();
        Boolean isInsuranceEnabled = scoringDataDTO.getIsInsuranceEnabled();
        Boolean isSalaryClient = scoringDataDTO.getIsSalaryClient();

        BigDecimal rate = scoringService.calculateScoringRate(scoringDataDTO);

        if (rate.equals(BigDecimal.valueOf(denyRate))) return CreditDTO.builder()
                .amount(amount)
                .term(term)
                .monthlyPayment(BigDecimal.ZERO)
                .rate(rate)
                .psk(BigDecimal.ZERO)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .paymentSchedule(new ArrayList<>())
                .build();

        BigDecimal totalAmount = scoringService.evaluateTotalAmount(amount, isInsuranceEnabled);
        BigDecimal monthlyPayment = scoringService.calculateMonthlyPayment(amount, term, rate);
        BigDecimal psk = scoringService.calculatePsk(amount, monthlyPayment, term, isInsuranceEnabled);
        List<PaymentScheduleElement> paymentSchedule = scoringService.paymentScheduleBuild(amount, term, rate, monthlyPayment);

        return CreditDTO.builder()
                .amount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .psk(psk)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .paymentSchedule(paymentSchedule)
                .build();
    }

}
