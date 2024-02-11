package com.home.account.service.impl;

import com.home.account.data.dto.AccountResponseDTO;
import com.home.account.data.dto.MessageResponseDTO;
import com.home.account.data.dto.TransactionResponseDTO;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.data.dto.TransferResponseDTO;
import com.home.account.data.enums.MessageStatus;
import com.home.account.data.enums.OperationType;
import com.home.account.data.model.Account;
import com.home.account.data.model.Operation;
import com.home.account.data.model.Transaction;
import com.home.account.repository.AccountRepository;
import com.home.account.repository.OperationRepository;
import com.home.account.repository.TransactionRepository;
import com.home.account.service.CashOperationsService;
import com.home.account.service.ExternalIntegrationService;
import com.home.account.service.vatidation.CashOperationsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashOperationsServiceImpl implements CashOperationsService {

    private static final String VALIDATION_MESSAGE = "Transaction not carried out. %s";

    public static final String TRANSACTION_SUCCESS_STATUS = "S";

    private final AccountRepository accountRepository;

    private final OperationRepository operationRepository;

    private final ExternalIntegrationService externalIntegrationService;

    private final TransactionRepository transactionRepository;

    private final CashOperationsValidator cashOperationsValidator;

    @Override
    @Transactional
    public MessageResponseDTO transfer(TransferRequestDTO transferRequestDTO) {

        var verificarion = transactionQuery(transferRequestDTO.getTransactionId());

        if (verificarion != null) {
            log.info("Transaction already made. TransactionId: {}", transferRequestDTO.getTransactionId());
            return verificarion;
        }

        var currentDateTime = LocalDateTime.now();

        var validation = cashOperationsValidator.validate(transferRequestDTO);

        if (!validation.success()) {

            var transaction = Transaction.builder()
                    .transactionId(transferRequestDTO.getTransactionId())
                    .transactionDateTime(currentDateTime)
                    .description(String.format(VALIDATION_MESSAGE, validation.failMessage()))
                    .operationType(OperationType.TRANSFER)
                    .success("N")
                    .build();

            transactionRepository.save(transaction);

            return MessageResponseDTO.builder()
                    .id(UUID.randomUUID().toString())
                    .description(String.format(VALIDATION_MESSAGE, validation.failMessage()))
                    .status(MessageStatus.FAIL)
                    .build();
        }

        var sourceAccount = validation.sourceAccount();
        sourceAccount.setBalance(sourceAccount.getBalance() - transferRequestDTO.getValue());
        sourceAccount.setLastTransferValue(sourceAccount.getLastTransferValue() + transferRequestDTO.getValue());
        sourceAccount.setLastDateTimeTransfer(currentDateTime);
        accountRepository.save(sourceAccount);

        var targetAccount = validation.targetAccount();
        targetAccount.setBalance(targetAccount.getBalance() + transferRequestDTO.getValue());
        accountRepository.save(targetAccount);

        var operation = Operation.builder()
                .transactionId(transferRequestDTO.getTransactionId())
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .operationDate(currentDateTime)
                .operationType(OperationType.TRANSFER)
                .value(transferRequestDTO.getValue())
                .build();

        operationRepository.save(operation);

        var transaction = Transaction.builder()
                .transactionId(transferRequestDTO.getTransactionId())
                .transactionDateTime(currentDateTime)
                .description("Transaction successfully completed")
                .operationType(OperationType.TRANSFER)
                .success("S")
                .build();

        transactionRepository.save(transaction);

        var transferResponseDTO = buildTransferResponseDTO(operation);

        externalIntegrationService.bacenRegister(transferResponseDTO);

        return MessageResponseDTO.builder()
                .id(UUID.randomUUID().toString())
                .description(transaction.getDescription())
                .status(MessageStatus.SUCCESS)
                .data(transferResponseDTO)
                .build();
    }

    @Override
    public MessageResponseDTO transactionQuery(String transactionId) {

        var transaction = transactionRepository.findByTransactionId(transactionId);

        if (transaction == null)
            return null;

        var transactionResponse = TransactionResponseDTO.builder()
                .transactionId(transaction.getTransactionId())
                .transactionDateTime(transaction.getTransactionDateTime())
                .description(transaction.getDescription())
                .success(transaction.getSuccess())
                .build();

        return MessageResponseDTO.builder()
                .id(UUID.randomUUID().toString())
                .description(transaction.getDescription())
                .status(TRANSACTION_SUCCESS_STATUS.equals(transaction.getSuccess()) ? MessageStatus.SUCCESS : MessageStatus.FAIL)
                .data(transactionResponse)
                .build();
    }

    private TransferResponseDTO buildTransferResponseDTO(final Operation operation) {

        return TransferResponseDTO.builder()
                .transactionId(operation.getTransactionId())
                .sourceAccount(buildAccountDTO(operation.getSourceAccount()))
                .targetAccount(buildAccountDTO(operation.getTargetAccount()))
                .value(operation.getValue())
                .operationType(operation.getOperationType())
                .operationDate(operation.getOperationDate())
                .build();
    }

    private AccountResponseDTO buildAccountDTO(final Account account) {
        var client = externalIntegrationService.findClientDetails(
                account.getClientDocument(),
                account.getDocumentType().toString()
        );

        return AccountResponseDTO.builder()
                .agency(account.getAgency())
                .accountNumber(account.getAccountNumber())
                .clientName(client.getName())
                .clientDocument(account.getClientDocument())
                .documentType(account.getDocumentType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .build();
    }
}
