package com.neoflex.java.dto;

import com.neoflex.java.enums.EmailMessageTheme;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Электронное письмо")
public class EmailMessage {
    private String address;
    private EmailMessageTheme emailMessageTheme;
    private Long applicationId;
}
