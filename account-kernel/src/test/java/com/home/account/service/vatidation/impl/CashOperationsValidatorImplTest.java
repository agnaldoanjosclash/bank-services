package com.home.account.service.vatidation.impl;

import com.home.account.data.dto.AccountRequestDTO;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.data.enums.AccountStatus;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.model.Account;
import com.home.account.repository.AccountRepository;
import com.home.account.service.vatidation.CashOperationsValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class CashOperationsValidatorImplTest {

    @Mock
    private AccountRepository accountRepository;

    private CashOperationsValidatorImpl validator;

    private Account sourceAccount, targetAccount;
    private TransferRequestDTO transferRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validator = new CashOperationsValidatorImpl(accountRepository);

        // Common setup for multiple tests
        AccountRequestDTO sourceAccountDTO = new AccountRequestDTO("agency", "accountNumber", "clientDocument", DocumentType.CPF);
        AccountRequestDTO targetAccountDTO = new AccountRequestDTO("targetAgency", "targetAccountNumber", "targetClientDocument", DocumentType.CPF);
        transferRequestDTO = new TransferRequestDTO("transactionId", sourceAccountDTO, targetAccountDTO, 500.0, null);

        sourceAccount = new Account(1L, "agency", "accountNumber", "clientDocument", DocumentType.CPF, 1000.0, AccountStatus.ACTIVE, 0.0, LocalDateTime.now());
        targetAccount = new Account(2L, "targetAgency", "targetAccountNumber", "targetClientDocument", DocumentType.CPF, 2000.0, AccountStatus.ACTIVE, 0.0, LocalDateTime.now());
    }

    @Test
    void whenSourceAccountNotFound_thenFailValidation() {
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(anyString(), anyString(), anyString(), any(DocumentType.class)))
                .thenReturn(null);

        CashOperationsValidation result = validator.validate(transferRequestDTO);

        assertFalse(result.success());
        assertEquals("Origin account not found.", result.failMessage());
    }

    @Test
    void whenSourceAccountInactive_thenFailValidation() {
        sourceAccount.setStatus(AccountStatus.INACTIVE);
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(anyString(), anyString(), anyString(), any(DocumentType.class)))
                .thenReturn(sourceAccount);

        CashOperationsValidation result = validator.validate(transferRequestDTO);

        assertFalse(result.success());
        assertEquals("Inactive source account.", result.failMessage());
    }

    @Test
    void whenInsufficientBalance_thenFailValidation() {
        sourceAccount.setBalance(transferRequestDTO.getValue() - 1);
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(anyString(), anyString(), anyString(), any(DocumentType.class)))
                .thenReturn(sourceAccount);

        CashOperationsValidation result = validator.validate(transferRequestDTO);

        assertFalse(result.success());
        assertEquals("Insufficient balance.", result.failMessage());
    }

    @Test
    void whenTargetAccountNotFound_thenFailValidation() {
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(anyString(), anyString(), anyString(), any(DocumentType.class)))
                .thenReturn(sourceAccount)
                .thenReturn(null);

        CashOperationsValidation result = validator.validate(transferRequestDTO);

        assertFalse(result.success());
        assertEquals("Target account not found.", result.failMessage());
    }

    @Test
    void whenTargetAccountInactive_thenFailValidation() {
        targetAccount.setStatus(AccountStatus.INACTIVE);
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(anyString(), anyString(), anyString(), any(DocumentType.class)))
                .thenReturn(sourceAccount)
                .thenReturn(targetAccount);

        CashOperationsValidation result = validator.validate(transferRequestDTO);

        assertFalse(result.success());
        assertEquals("Inactive target account.", result.failMessage());
    }

    @Test
    void whenDailyLimitExceeded_thenFailValidation() {
        sourceAccount.setLastTransferValue(CashOperationsValidatorImpl.DAILY_LIMIT_FOR_TRANSFERS - transferRequestDTO.getValue() + 1);
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(anyString(), anyString(), anyString(), any(DocumentType.class)))
                .thenReturn(sourceAccount)
                .thenReturn(targetAccount);

        CashOperationsValidation result = validator.validate(transferRequestDTO);

        assertFalse(result.success());
        assertEquals("Daily transfer limit exceeded.", result.failMessage());
    }

    @Test
    void whenAllValidationsPass_thenSucceed() {
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(anyString(), anyString(), anyString(), any(DocumentType.class)))
                .thenReturn(sourceAccount)
                .thenReturn(targetAccount);

        CashOperationsValidation result = validator.validate(transferRequestDTO);

        assertTrue(result.success());
        assertEquals("Validations performed successfully.", result.failMessage());
    }
}
