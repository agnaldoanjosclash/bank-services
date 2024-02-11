package com.home.account.service.vatidation.impl;

import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.data.enums.AccountStatus;
import com.home.account.repository.AccountRepository;
import com.home.account.service.vatidation.CashOperationsValidation;
import com.home.account.service.vatidation.CashOperationsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashOperationsValidatorImpl implements CashOperationsValidator {

    public static final int DAILY_LIMIT_FOR_TRANSFERS = 1000;

    private final AccountRepository accountRepository;

    @Override
    public CashOperationsValidation validate(TransferRequestDTO transferRequestDTO) {

        var sourceAccount = accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(
                transferRequestDTO.getSourceAccount().getAgency(),
                transferRequestDTO.getSourceAccount().getAccountNumber(),
                transferRequestDTO.getSourceAccount().getClientDocument(),
                transferRequestDTO.getSourceAccount().getDocumentType()
        );

        if (sourceAccount == null) {
            return  new CashOperationsValidation(
                    sourceAccount,
                    null,
                    false,
                    "Origin account not found.");
        }

        sourceAccount = sourceAccount.evaluateTransferInformation();

        if (sourceAccount.getStatus() == AccountStatus.INACTIVE) {
            return  new CashOperationsValidation(
                    sourceAccount,
                    null,
                    false,
                    "Inactive source account.");
        }

        if (sourceAccount.getBalance() < transferRequestDTO.getValue()) {
            return new CashOperationsValidation(
                    sourceAccount,
                    null,
                    false,
                    "Insufficient balance."
            );
        }

        var targetAccount = accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(
                transferRequestDTO.getTargetAccount().getAgency(),
                transferRequestDTO.getTargetAccount().getAccountNumber(),
                transferRequestDTO.getTargetAccount().getClientDocument(),
                transferRequestDTO.getTargetAccount().getDocumentType()
        );

        if (targetAccount == null) {
            return  new CashOperationsValidation(
                    sourceAccount,
                    null,
                    false,
                    "Target account not found.");
        }

        if (targetAccount.getStatus() == AccountStatus.INACTIVE) {
            return new CashOperationsValidation(
                    sourceAccount,
                    targetAccount,
                    false,
                    "Inactive target account."
            );
        }

        final Double finalTotalValue = sourceAccount.getLastTransferValue() + transferRequestDTO.getValue();

        if (finalTotalValue.doubleValue() > DAILY_LIMIT_FOR_TRANSFERS) {
            return  new CashOperationsValidation(
                    sourceAccount,
                    targetAccount,
                    false,
                    "Daily transfer limit exceeded."
            );
        }

        return  new CashOperationsValidation(
                sourceAccount,
                targetAccount,
                true,
                "Validations performed successfully."
        );
    }
}
