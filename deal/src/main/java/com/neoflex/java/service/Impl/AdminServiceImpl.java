package com.neoflex.java.service.Impl;

import com.neoflex.java.model.Application;
import com.neoflex.java.repository.ApplicationRepository;
import com.neoflex.java.service.AdminService;
import com.neoflex.java.util.CustomLogger;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ApplicationRepository applicationRepository;
    private static final int PAGE_SIZE = 1000;

    @Override
    public Application findApplicationById(Long id) {
        CustomLogger.logInfoClassAndMethod();
        Optional<Application> optionalApplication = applicationRepository.findById(id);
        return optionalApplication.orElseGet(() -> Application.builder().build());
    }

    @Override
    public List<Application> findPageApplication() {
        CustomLogger.logInfoClassAndMethod();
        Pageable firstPage = PageRequest.of(0, PAGE_SIZE);
        Page<Application> applicationPage = applicationRepository.findAll(firstPage);
        return applicationPage.getContent();
    }
}
