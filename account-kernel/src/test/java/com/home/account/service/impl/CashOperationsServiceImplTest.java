package com.home.account.service.impl;

import com.home.account.data.dto.AccountRequestDTO;
import com.home.account.data.dto.MessageResponseDTO;
import com.home.account.data.dto.RegistrationResquestDTO;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.data.enums.AccountStatus;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.enums.MessageStatus;
import com.home.account.data.enums.OperationType;
import com.home.account.data.model.Account;
import com.home.account.data.model.Operation;
import com.home.account.data.model.Transaction;
import com.home.account.repository.AccountRepository;
import com.home.account.repository.OperationRepository;
import com.home.account.repository.TransactionRepository;
import com.home.account.service.ExternalIntegrationService;
import com.home.account.service.vatidation.CashOperationsValidation;
import com.home.account.service.vatidation.CashOperationsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


public class CashOperationsServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ExternalIntegrationService externalIntegrationService;

    @Mock
    private CashOperationsValidator cashOperationsValidator;


    private CashOperationsServiceImpl service;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service = new CashOperationsServiceImpl(accountRepository, operationRepository, externalIntegrationService, transactionRepository, cashOperationsValidator);

    }

    @Test
    void whenTransferIsCalledWithValidRequest_thenSucceed() {
        var transferRequestDTO = TransferRequestDTO.builder()
                .transactionId(UUID.randomUUID().toString())
                .sourceAccount(AccountRequestDTO.builder()
                        .accountNumber("12345")
                        .agency("1234")
                        .documentType(DocumentType.CPF)
                        .clientDocument("12345678900")
                        .build())
                .targetAccount(AccountRequestDTO.builder()
                        .accountNumber("67890")
                        .agency("5678")
                        .documentType(DocumentType.CPF)
                        .clientDocument("12345678901")
                        .build())
                .value(100.0)
                .operationType(OperationType.TRANSFER)
                .build();
        var validation = new CashOperationsValidation(
                Account.builder()
                        .id(1L)
                        .accountNumber("12345")
                        .agency("1234")
                        .clientDocument("12345678900")
                        .documentType(DocumentType.CPF)
                        .status(AccountStatus.ACTIVE)
                        .balance(1000.0)
                        .lastTransferValue(0.0)
                        .lastDateTimeTransfer(LocalDateTime.now())
                        .build(),
                Account.builder()
                        .id(2L)
                        .accountNumber("67890")
                        .agency("5678")
                        .clientDocument("12345678901")
                        .documentType(DocumentType.CPF)
                        .status(AccountStatus.ACTIVE)
                        .balance(1000.0)
                        .lastTransferValue(0.0)
                        .lastDateTimeTransfer(LocalDateTime.now())
                        .build(),
                true, "Validations performed successfully."
        );
        var client = RegistrationResquestDTO.builder()
                .documentType("CPF")
                .document("12345678900")
                .name("Test User")
                .build();

        when(transactionRepository.findByTransactionId(any())).thenReturn(null);
        when(cashOperationsValidator.validate(any(TransferRequestDTO.class))).thenReturn(validation);
        when(accountRepository.save(any())).thenReturn(Account.builder().build());
        when(operationRepository.save(any())).thenReturn(Operation.builder().build());
        when(externalIntegrationService.bacenRegister(any())).thenReturn(true);
        when(externalIntegrationService.findClientDetails(any(), any())).thenReturn(client);


        // Mock externalIntegrationService, accountRepository, and operationRepository calls as necessary

        // Call the method under test
        MessageResponseDTO response = service.transfer(transferRequestDTO);

        // Assert the expected outcomes, e.g., success status, correct data in response
        assertNotNull(response);
        assertEquals(MessageStatus.SUCCESS, response.getStatus());

        // Verify interactions with mocks
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(operationRepository, times(1)).save(any(Operation.class));
        verify(accountRepository, times(2)).save(any(Account.class)); // For both source and target accounts
    }

    @Test
    void whenTransferIsCalledAndValidationFails_thenFail() {
        var transferRequestDTO = TransferRequestDTO.builder()
                .transactionId(UUID.randomUUID().toString())
                .sourceAccount(AccountRequestDTO.builder()
                        .accountNumber("12345")
                        .agency("1234")
                        .documentType(DocumentType.CPF)
                        .clientDocument("12345678900")
                        .build())
                .targetAccount(AccountRequestDTO.builder()
                        .accountNumber("67890")
                        .agency("5678")
                        .documentType(DocumentType.CPF)
                        .clientDocument("12345678901")
                        .build())
                .value(100.0)
                .operationType(OperationType.TRANSFER)
                .build();

        var validation = new CashOperationsValidation(
                Account.builder()
                        .id(1L)
                        .accountNumber("12345")
                        .agency("1234")
                        .clientDocument("12345678900")
                        .documentType(DocumentType.CPF)
                        .status(AccountStatus.ACTIVE)
                        .balance(1000.0)
                        .lastTransferValue(0.0)
                        .lastDateTimeTransfer(LocalDateTime.now())
                        .build(),
                Account.builder()
                        .id(2L)
                        .accountNumber("67890")
                        .agency("5678")
                        .clientDocument("12345678901")
                        .documentType(DocumentType.CPF)
                        .status(AccountStatus.ACTIVE)
                        .balance(1000.0)
                        .lastTransferValue(0.0)
                        .lastDateTimeTransfer(LocalDateTime.now())
                        .build(),
                false, "Validation Failed"
        );

        // Setup your mocks to simulate validation failure
        var client = RegistrationResquestDTO.builder()
                .documentType("CPF")
                .document("12345678900")
                .name("Test User")
                .build();

        when(transactionRepository.findByTransactionId(any())).thenReturn(null);
        when(cashOperationsValidator.validate(any(TransferRequestDTO.class))).thenReturn(validation);
        when(accountRepository.save(any())).thenReturn(Account.builder().build());
        when(operationRepository.save(any())).thenReturn(Operation.builder().build());
        when(externalIntegrationService.bacenRegister(any())).thenReturn(true);
        when(externalIntegrationService.findClientDetails(any(), any())).thenReturn(client);

        // Call the method under test
        MessageResponseDTO response = service.transfer(transferRequestDTO);

        // Assert the expected failure outcomes
        assertNotNull(response);
        assertEquals(MessageStatus.FAIL, response.getStatus());
        assertTrue(response.getDescription().contains("Validation Failed"));

        // Verify interactions with mocks
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verifyNoInteractions(operationRepository); // No operations should be saved on validation failure
    }

    @Test
    void whenTransactionQueryIsCalledWithExistingTransaction_thenRetrieveDetails() {
        var transaction = Transaction.builder()
                .id(1L)
                .transactionId(UUID.randomUUID().toString())
                .description("Test Transaction")
                .transactionDateTime(LocalDateTime.now())
                .operationType(OperationType.TRANSFER)
                .success("S")
                .build();
        // Setup your mocks to return a pre-existing transaction
        when(transactionRepository.findByTransactionId(transaction.getTransactionId()))
                .thenReturn(transaction);

        // Call the method under test
        MessageResponseDTO response = service.transactionQuery(transaction.getTransactionId());

        // Assert the expected outcomes
        assertNotNull(response);
        assertNotNull(response.getData());

        // Verify interactions with mocks
        verify(transactionRepository, times(1)).findByTransactionId(transaction.getTransactionId());
    }
}
