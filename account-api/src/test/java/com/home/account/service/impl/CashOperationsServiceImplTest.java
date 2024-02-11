package com.home.account.service.impl;

import com.home.account.config.KafkaConfiguration;
import com.home.account.data.dto.AccountRequestDTO;
import com.home.account.data.dto.MessageResponseDTO;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.enums.MessageStatus;
import com.home.account.data.enums.OperationType;
import com.home.account.integration.AccountKernelClient;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashOperationsServiceImplTest {

    @Mock
    private KafkaTemplate<String, TransferRequestDTO> kafkaTemplate;

    @Mock
    private AccountKernelClient accountKernelClient;

    @InjectMocks
    private CashOperationsServiceImpl cashOperationsService;

    private TransferRequestDTO transferRequestDTO;

    @BeforeEach
    void setUp() {
        transferRequestDTO = TransferRequestDTO.builder()
                .transactionId(UUID.randomUUID().toString())
                .sourceAccount(
                        new AccountRequestDTO("0001", "12345", "00000000001", DocumentType.CPF)
                )
                .targetAccount(
                        new AccountRequestDTO("0002", "67890", "00000000002", DocumentType.CPF)
                )
                .value(100.0)
                .operationType(OperationType.TRANSFER)
                .build();
    }

    @Test
    void whenTransferIsSuccessful_thenReturnsSuccess() {
        when(accountKernelClient.transaction(anyString()))
                .thenReturn(ResponseEntity.ok(
                        new MessageResponseDTO(UUID.randomUUID().toString(),
                                "Transfer processed successfully",
                                MessageStatus.SUCCESS,
                                transferRequestDTO
                        )));

        MessageResponseDTO result = cashOperationsService.transfer(transferRequestDTO);

        assertNotNull(result);
        assertEquals(MessageStatus.SUCCESS, result.getStatus());
        verify(kafkaTemplate, times(1)).send(eq(KafkaConfiguration.CASH_OPERATION_TOPIC), anyInt(), anyString(), eq(transferRequestDTO));
    }

    @Test
    void whenTransferExceedsRetryAttempts_thenReturnsFail() {
        when(accountKernelClient.transaction(anyString())).thenThrow(FeignException.NotFound.class);
        when(kafkaTemplate.send(anyString(), anyInt(), anyString(), any(TransferRequestDTO.class))).thenReturn(null);

        MessageResponseDTO result = cashOperationsService.transfer(transferRequestDTO);

        assertNotNull(result);
        assertEquals(MessageStatus.FAIL, result.getStatus());
        assertEquals("Transfer timeout, try again later.", result.getDescription());
        verify(kafkaTemplate, times(1)).send(eq(KafkaConfiguration.CASH_OPERATION_TOPIC), anyInt(), anyString(), eq(transferRequestDTO));
    }

    @Test
    void whenTransferIsInterrupted_thenReturnsFail() throws InterruptedException {
//        doThrow(InterruptedException.class).when(accountKernelClient).transaction(anyString());
//
        when(accountKernelClient.transaction(transferRequestDTO.getTransactionId())).thenReturn(
                ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        );
        when(kafkaTemplate.send(anyString(), anyInt(), anyString(), any(TransferRequestDTO.class))).thenReturn(null);

        MessageResponseDTO result = cashOperationsService.transfer(transferRequestDTO);


        assertNotNull(result);
        assertEquals(MessageStatus.FAIL, result.getStatus());
        assertEquals("Transfer timeout, try again later.", result.getDescription());
        verify(kafkaTemplate, times(1)).send(eq(KafkaConfiguration.CASH_OPERATION_TOPIC), anyInt(), anyString(), eq(transferRequestDTO));
    }
}
