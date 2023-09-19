package com.neoflex.java.feign;

import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.dto.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "application", url = "${application.destination}")
public interface FeignApplication {

    @PostMapping
    List<LoanOfferDTO> getOffers(@RequestBody LoanApplicationRequestDTO request);

    @PutMapping("offer")
    void specifyApplication(@RequestBody LoanOfferDTO request);

}
