package com.home.account.service;

import com.home.account.data.dto.TransferRequestDTO;

public interface ExternalIntegrationService {

    boolean bacenRegister(TransferRequestDTO transferResponseDTO);
}
