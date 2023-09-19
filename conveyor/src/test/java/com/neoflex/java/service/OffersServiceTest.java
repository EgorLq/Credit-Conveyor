package com.neoflex.java.service;

import com.neoflex.java.dto.*;
import com.neoflex.java.enums.EmploymentPosition;
import com.neoflex.java.enums.EmploymentStatus;
import com.neoflex.java.enums.MaritalStatus;
import com.neoflex.java.service.Impl.OffersServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {OffersServiceImpl.class})
@ComponentScan("com.neoflex.java")
class OffersServiceTest {

    private final Double baseRate;
    private final Integer denyRate;
    private final Integer insurancePrice;
    private final Double insuranceRateDiscount;
    private final Double salaryClientRateDiscount;

    public OffersServiceTest(@Value("${baseRate}") Double baseRate,
                             @Value("${denyRate}") Integer denyRate,
                             @Value("${insurancePrice}") Integer insurancePrice,
                             @Value("${insuranceRateDiscount}") Double insuranceRateDiscount,
                             @Value("${salaryClientRateDiscount}") Double salaryClientRateDiscount) {
        this.baseRate = baseRate;
        this.denyRate = denyRate;
        this.insurancePrice = insurancePrice;
        this.insuranceRateDiscount = insuranceRateDiscount;
        this.salaryClientRateDiscount = salaryClientRateDiscount;
    }

    @MockBean
    ScoringService scoringService;

    @Autowired
    private OffersService offersService;

    @Test
    void getPreparedOffers() {
        BigDecimal amount = BigDecimal.valueOf(300000);
        BigDecimal amountWithInsurance = amount.subtract(BigDecimal.valueOf(insurancePrice));

        LoanApplicationRequestDTO dto = LoanApplicationRequestDTO.builder()
                .amount(amount)
                .term(18)
                .firstName("Vasiliy")
                .lastName("Petrov")
                .email("vas@gmail.com")
                .birthdate(LocalDate.parse("1977-08-16"))
                .passportSeries("5766")
                .passportNumber("576687")
                .build();

        when(scoringService.calculatePrescoringRate(anyBoolean(), anyBoolean())).thenReturn(BigDecimal.valueOf(baseRate)
                        .subtract(BigDecimal.valueOf(insuranceRateDiscount + salaryClientRateDiscount)),
                BigDecimal.valueOf(baseRate).subtract(BigDecimal.valueOf(insuranceRateDiscount)),
                BigDecimal.valueOf(baseRate).subtract(BigDecimal.valueOf(salaryClientRateDiscount)),
                BigDecimal.valueOf(baseRate));
        when(scoringService.calculateMonthlyPayment(amount, 18, BigDecimal.valueOf(baseRate))).thenReturn(BigDecimal.valueOf(18714.44));
        when(scoringService.evaluateTotalAmount(amount, false)).thenReturn(amount);
        when(scoringService.evaluateTotalAmount(amount, true)).thenReturn(amountWithInsurance);

        List<LoanOfferDTO> result = offersService.createPrescoringOffers(dto);

        assertEquals(4, result.size());
        assertEquals(amount, result.get(0).getRequestedAmount());
        verify(scoringService, Mockito.times(4)).evaluateTotalAmount(any(), anyBoolean());
        verify(scoringService, Mockito.times(4)).calculatePrescoringRate(anyBoolean(), anyBoolean());

        Assertions.assertDoesNotThrow(() -> offersService.createPrescoringOffers(dto));

        assertEquals(amountWithInsurance, result.get(3).getTotalAmount());
        assertEquals(amountWithInsurance, result.get(2).getTotalAmount());
        assertEquals(amount, result.get(1).getTotalAmount());
        assertEquals(amount, result.get(0).getTotalAmount());

    }

    @Test
    void createCreditOffer() {
        BigDecimal amount = BigDecimal.valueOf(20000);
        Integer term = 36;
        BigDecimal monthlyPayment = BigDecimal.valueOf(24970);
        BigDecimal rate = BigDecimal.valueOf(12.0);
        BigDecimal psk = BigDecimal.valueOf(14.978);

        EmploymentDTO employmentDTO = EmploymentDTO.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .salary(BigDecimal.valueOf(100000))
                .position(EmploymentPosition.WORKER)
                .workExperienceTotal(120)
                .workExperienceCurrent(120)
                .build();

        ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
                .amount(amount)
                .term(term)
                .firstName("Vasiliy")
                .lastName("Petrov")
                .birthdate(LocalDate.parse("1977-08-16"))
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(1)
                .employment(employmentDTO)
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        when(scoringService.calculateScoringRate(scoringDataDTO)).thenReturn(rate);
        when(scoringService.calculateMonthlyPayment(amount, term, rate)).thenReturn(monthlyPayment);
        when(scoringService.calculatePsk(amount, monthlyPayment, term, false)).thenReturn(psk);

        CreditDTO creditDTO = offersService.createCreditOffer(scoringDataDTO);

        assertEquals(monthlyPayment, creditDTO.getMonthlyPayment());
        assertEquals(rate, creditDTO.getRate());
        assertEquals(psk, creditDTO.getPsk());

        scoringDataDTO.setBirthdate(LocalDate.parse("2007-08-16"));

        when(scoringService.calculateScoringRate(scoringDataDTO)).thenReturn(BigDecimal.valueOf(999));

        creditDTO = offersService.createCreditOffer(scoringDataDTO);
        assertEquals(BigDecimal.valueOf(denyRate), creditDTO.getRate());
    }
}