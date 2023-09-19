package com.neoflex.java.service;

import com.neoflex.java.dto.LoanApplicationRequestDTO;
import com.neoflex.java.model.Application;
import com.neoflex.java.model.Client;

public interface ApplicationBuildService {
    Client createClient(LoanApplicationRequestDTO request);

    Application createApplication(Client client);
}
