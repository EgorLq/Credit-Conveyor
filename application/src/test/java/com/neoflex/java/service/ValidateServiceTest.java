package com.neoflex.java.service;

import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.service.Impl.ValidateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ValidateServiceImpl.class})
@ComponentScan("com.neoflex.java")
@ExtendWith(OutputCaptureExtension.class)
class ValidateServiceTest {

    @Autowired
    private ValidateService validateService;

    private final LoanApplicationRequestDTO dto = LoanApplicationRequestDTO.builder()
            .firstName("Vasiliy")
            .lastName("Petrov")
            .birthdate(LocalDate.parse("1977-08-16"))
            .term(12)
            .email("vas@gmail.com")
            .passportSeries("5766")
            .passportNumber("576687")
            .build();

    @Test
    void validateIsOk() {
        assertTrue(validateService.validatePrescoringRequest(dto));
    }

    @Test
    void validateFirstNameFail(CapturedOutput output) {
        dto.setFirstName("V");
        assertFalse(validateService.validatePrescoringRequest(dto));
        assertTrue(output.getOut().contains("Имя не правильной длины"));
        dto.setFirstName("Vasiliy");
    }

    @Test
    void validateBirthday(CapturedOutput output) {
        dto.setBirthdate(LocalDate.parse("2007-08-16"));
        assertFalse(validateService.validatePrescoringRequest(dto));
        assertTrue(output.getOut().contains("Нет 18 лет"));
        dto.setBirthdate(LocalDate.parse("1977-08-16"));
    }

    @Test
    void validateEmail(CapturedOutput output) {
        dto.setEmail("v@gmail.com");
        assertFalse(validateService.validatePrescoringRequest(dto));
        assertTrue(output.getOut().contains("Неправильный email"));
        dto.setEmail("vas@gmail.com");
    }

    @Test
    void validatePassportSeries(CapturedOutput output) {
        dto.setPassportSeries("5767676");
        assertFalse(validateService.validatePrescoringRequest(dto));
        assertTrue(output.getOut().contains("Неправильная серия паспорта"));
        dto.setPassportSeries("5766");
    }
}
