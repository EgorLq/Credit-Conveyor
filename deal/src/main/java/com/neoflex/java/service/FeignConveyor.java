package com.neoflex.java.service;

import com.neoflex.java.dto.CreditDTO;
import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.dto.LoanOfferDTO;
import com.neoflex.java.dto.ScoringDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "conveyor", url = "${conveyor.destination}")
public interface FeignConveyor {

    @PostMapping("offers")
    List<LoanOfferDTO> getCreatedOffers(@RequestBody LoanApplicationRequestDTO request);

    @PostMapping("calculation")
    CreditDTO getCalculatedCredit(@RequestBody ScoringDataDTO request);
}
