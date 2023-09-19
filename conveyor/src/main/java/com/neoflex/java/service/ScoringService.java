package com.neoflex.java.service;

import com.neoflex.java.dto.PaymentScheduleElement;
import com.neoflex.java.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ScoringService {
    BigDecimal evaluateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled);

    BigDecimal calculatePrescoringRate(boolean isInsuranceEnabled, boolean isSalaryClient);

    BigDecimal calculateMonthlyPayment(BigDecimal amount, Integer term, BigDecimal rate);

    BigDecimal calculatePsk(BigDecimal amount, BigDecimal monthlyPayment, Integer term, boolean isInsuranceEnabled);

    List<PaymentScheduleElement> paymentScheduleBuild(BigDecimal amount, Integer term, BigDecimal rate, BigDecimal monthlyPayment);

    BigDecimal calculateScoringRate(ScoringDataDTO scoringDataDTO);
}
