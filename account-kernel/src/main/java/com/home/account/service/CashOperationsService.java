package com.home.account.service;

import com.home.account.data.dto.MessageResponseDTO;
import com.home.account.data.dto.TransferRequestDTO;

public interface CashOperationsService {
    MessageResponseDTO transfer(TransferRequestDTO transferRequestDTO);

    MessageResponseDTO transactionQuery(String transactionId);

}
