package com.home.account.service;

import com.home.account.data.dto.RegistrationResponseDTO;
import com.home.account.data.dto.TransferResponseDTO;

public interface ExternalIntegrationService {
    RegistrationResponseDTO findClientDetails(String document, String type);

    boolean bacenRegister(TransferResponseDTO transferResponseDTO);
}
