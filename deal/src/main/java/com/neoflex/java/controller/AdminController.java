package com.neoflex.java.controller;

import com.neoflex.java.model.Application;
import com.neoflex.java.service.AdminService;
import com.neoflex.java.util.CustomLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер микросервиса сделки
 */
@RestController
@RequestMapping("/deal/admin/application")
@AllArgsConstructor
@SuppressWarnings("unused")
@Tag(name = "AdminController", description = "Контроллер администратора")
public class AdminController {
    private AdminService adminService;

    @Operation(summary = "Получить заявку по id")
    @PostMapping(value = "/{applicationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Application getApplication(@RequestParam long id) {
        CustomLogger.logInfoClassAndMethod();
        CustomLogger.logInfoRequest(id);
        return adminService.findApplicationById(id);
    }

    @Operation(summary = "Получить все заявки")
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Application> getAllApplications() {
        CustomLogger.logInfoClassAndMethod();
        return adminService.findPageApplication();
    }
}
