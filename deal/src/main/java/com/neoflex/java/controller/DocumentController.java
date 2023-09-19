package com.neoflex.java.controller;

import com.neoflex.java.enums.ApplicationStatus;
import com.neoflex.java.enums.EmailMessageTheme;
import com.neoflex.java.service.DealService;
import com.neoflex.java.util.CustomLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Контроллер микросервиса сделки для работы с документами
 */
@Controller
@RequestMapping("/deal/document")
@AllArgsConstructor
@SuppressWarnings("unused")
@Tag(name = "DocumentController", description = "Контроллер микросервиса сделки для работы с документами")
public class DocumentController {
    private DealService dealService;

    @Operation(summary = "Запрос на отправку документов")
    @PostMapping(value = "/{applicationId}/send")
    @ResponseStatus(value = HttpStatus.OK)
    public void requestDocuments(@RequestParam long id) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(id);
        dealService.sendMessage(ApplicationStatus.PREPARE_DOCUMENTS, EmailMessageTheme.SEND_DOCUMENTS, id);
    }

    @Operation(summary = "Запрос на подписание документов")
    @PostMapping(value = "/{applicationId}/sign")
    @ResponseStatus(value = HttpStatus.OK)
    public void signDocuments(@RequestParam long id) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(id);
        dealService.sendMessage(ApplicationStatus.DOCUMENT_SIGNED, EmailMessageTheme.SEND_SES, id);
    }

    @Operation(summary = "Подписание документов")
    @PostMapping(value = "/{applicationId}/code")
    @ResponseStatus(value = HttpStatus.OK)
    public void acceptCode(@RequestParam long id) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(id);
        dealService.sendMessage(ApplicationStatus.CREDIT_ISSUED, EmailMessageTheme.CREDIT_ISSUED, id);
    }
}
