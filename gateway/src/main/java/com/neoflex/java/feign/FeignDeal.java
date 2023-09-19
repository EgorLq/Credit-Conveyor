package com.neoflex.java.feign;

import com.neoflex.java.dto.CreditDTO;
import com.neoflex.java.dto.FinishRegistrationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "deal", url = "${deal.destination}")
public interface FeignDeal {

    @PutMapping(value = "/calculate/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CreditDTO finishCalculateCredit(@RequestBody FinishRegistrationRequestDTO request, @RequestParam long id);

    @PostMapping(value = "/document/{id}/send")
    void requestDocuments(@RequestParam long id);

    @PostMapping(value = "/document/{id}/sign")
    void signDocuments(@RequestParam long id);

    @PostMapping(value = "/document/{id}/code")
    void acceptCode(@RequestParam long id);
}
