package com.neoflex.java.mapper;

import com.neoflex.java.dto.CreditDTO;
import com.neoflex.java.model.Credit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit mapCredit(CreditDTO creditDTO);
}
