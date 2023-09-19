package com.neoflex.java.service;

import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.dto.LoanOfferDTO;
import com.neoflex.java.feign.FeignDeal;
import com.neoflex.java.service.Impl.PrescoringServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {PrescoringServiceImpl.class})
@ComponentScan("com.neoflex.java")
@ExtendWith(OutputCaptureExtension.class)
class PrescoringServiceTest {

    @Autowired
    private PrescoringService prescoringService;

    @MockBean
    private ValidateService validateService;

    @MockBean
    private FeignDeal feignDeal;

    @Test
    void getPreparedOffers() {
        LoanApplicationRequestDTO dto = LoanApplicationRequestDTO.builder()
                .build();
        List<LoanOfferDTO> expectedList = new ArrayList<>();
        expectedList.add(LoanOfferDTO.builder()
                .totalAmount(BigDecimal.TEN)
                .build());

        when(feignDeal.getOffers(any())).thenReturn(expectedList);
        when(validateService.validatePrescoringRequest(any())).thenReturn(true);
        List<LoanOfferDTO> resultList = prescoringService.getPreparedOffers(dto);
        assertEquals(1, resultList.size());
        assertEquals(BigDecimal.TEN, resultList.get(0).getTotalAmount());

        when(validateService.validatePrescoringRequest(any())).thenReturn(false);
        assertEquals(0, prescoringService.getPreparedOffers(dto).size());
    }

    @Test
    void executeSpecifyApplicationRequest(CapturedOutput output) {
        LoanOfferDTO dto = LoanOfferDTO.builder().build();

        prescoringService.executeSpecifyApplicationRequest(dto);
        assertTrue(output.getOut().contains("Заявка обработана"));

        assertThrows(Exception.class, () -> doThrow(new Exception()).when(feignDeal).specifyApplication(any()));
        prescoringService.executeSpecifyApplicationRequest(dto);
        assertTrue(output.getOut().contains("""
                Ошибка сети\s
                Checked exception is invalid for this method!
                Invalid: java.lang.Exception"""));
    }
}