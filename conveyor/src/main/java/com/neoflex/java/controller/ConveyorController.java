package com.neoflex.java.controller;

import com.neoflex.java.dto.CreditDTO;
import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.dto.LoanOfferDTO;
import com.neoflex.java.dto.ScoringDataDTO;
import com.neoflex.java.service.OffersService;
import com.neoflex.java.util.CustomLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер кредитного конвейра
 */
@RestController
@RequestMapping("/conveyor")
@AllArgsConstructor
@SuppressWarnings("unused")
@Tag(name = "ConveyorController", description = "Контроллер кредитного конвейра")
public class ConveyorController {
    private OffersService offersService;

    @Operation(summary = "Расчёт возможных условий кредита")
    @PostMapping(value = "/offers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LoanOfferDTO> createOffers(@RequestBody LoanApplicationRequestDTO request) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(request);
        return offersService.createPrescoringOffers(request);
    }

    @Operation(summary = "Валидация присланных данных + скоринг данных + полный расчет параметров кредита")
    @PostMapping(value = "/calculation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CreditDTO calculateCredit(@RequestBody ScoringDataDTO request) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(request);
        return offersService.createCreditOffer(request);
    }
}
