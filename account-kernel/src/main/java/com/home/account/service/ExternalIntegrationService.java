package com.home.account.service;

import com.home.account.data.dto.RegistrationResquestDTO;
import com.home.account.data.dto.TransferResponseDTO;

public interface ExternalIntegrationService {
    RegistrationResquestDTO findClientDetails(String document, String type);

    boolean bacenRegister(TransferResponseDTO transferResponseDTO);
}
