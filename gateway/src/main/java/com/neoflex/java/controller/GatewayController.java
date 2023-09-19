package com.neoflex.java.controller;

import com.neoflex.java.dto.CreditDTO;
import com.neoflex.java.dto.FinishRegistrationRequestDTO;
import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.dto.LoanOfferDTO;
import com.neoflex.java.feign.FeignApplication;
import com.neoflex.java.feign.FeignDeal;
import com.neoflex.java.util.CustomLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер микросервиса шлюза
 */
@Controller
@RequestMapping("/gateway")
@AllArgsConstructor
@SuppressWarnings("unused")
@Tag(name = "GatewayController", description = "Контроллер микросервиса API-Gateway")
public class GatewayController {
    private FeignApplication feignApplication;
    private FeignDeal feignDeal;

    @Operation(summary = "Прескоринг + Расчёт возможных условий кредита")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<LoanOfferDTO> getOffers(@RequestBody LoanApplicationRequestDTO request) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(request);
        return feignApplication.getOffers(request);
    }

    @Operation(summary = "Выбор одного из предложений")
    @PutMapping(value = "/offer", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public void specifyApplication(@RequestBody LoanOfferDTO request) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(request);
        feignApplication.specifyApplication(request);
    }

    @Operation(summary = "Завершение регистрации + полный подсчёт кредита")
    @PutMapping(value = "/calculate/{applicationId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CreditDTO finishCalculateCredit(@RequestBody FinishRegistrationRequestDTO request, @RequestParam long id) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(request);
        return feignDeal.finishCalculateCredit(request, id);
    }

    @Operation(summary = "Запрос на отправку документов")
    @PostMapping(value = "/{applicationId}/send")
    @ResponseStatus(value = HttpStatus.OK)
    public void requestDocuments(@RequestParam long id) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(id);
        feignDeal.requestDocuments(id);
    }

    @Operation(summary = "Запрос на подписание документов")
    @PostMapping(value = "/{applicationId}/sign")
    @ResponseStatus(value = HttpStatus.OK)
    public void signDocuments(@RequestParam long id) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(id);
        feignDeal.signDocuments(id);
    }

    @Operation(summary = "Подписание документов")
    @PostMapping(value = "/{applicationId}/code")
    @ResponseStatus(value = HttpStatus.OK)
    public void acceptCode(@RequestParam long id) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(id);
        feignDeal.acceptCode(id);
    }
}
