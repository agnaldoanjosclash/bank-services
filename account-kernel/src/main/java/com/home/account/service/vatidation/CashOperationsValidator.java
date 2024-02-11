package com.home.account.service.vatidation;

import com.home.account.data.dto.TransferRequestDTO;

public interface CashOperationsValidator {

    CashOperationsValidation validate(TransferRequestDTO transferRequestDTO);
}
