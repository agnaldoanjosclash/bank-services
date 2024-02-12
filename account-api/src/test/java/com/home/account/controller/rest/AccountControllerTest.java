package com.home.account.controller.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.account.data.dto.AccountRequestDTO;
import com.home.account.data.dto.AccountResponseDTO;
import com.home.account.data.dto.MessageResponseDTO;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.data.dto.TransferResponseDTO;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.enums.MessageStatus;
import com.home.account.data.enums.OperationType;
import com.home.account.service.CashOperationsService;
import com.home.account.service.RegisterDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CashOperationsService cashOperationsService;

    @MockBean
    private RegisterDataService registerDataService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void whenDetails_thenReturnsAccountDetails() throws Exception {
        var accountResponseDTO = AccountResponseDTO.builder()
                .clientName("John Doe")
                .accountNumber("000001")
                .agency("0001")
                .clientDocument("12345678901")
                .documentType(DocumentType.CPF)
                .balance(1000.0)
                .build();
        when(registerDataService.findAccountDetails(any(), any(), any(), any())).thenReturn(accountResponseDTO);

        mockMvc.perform(get("/api/account/{number}/agency/{agency}/clientDocument/{clientDocument}/typeDocument/{typeDocument}/details",
                        "123", "001", "12345678901", "CPF"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.clientName").value(accountResponseDTO.getClientName()));
    }

    @Test
    void whenDetailsNotFound_thenReturnsNotFound() throws Exception {
        when(registerDataService.findAccountDetails(any(), any(), any(), any())).thenReturn(null);

        mockMvc.perform(get("/api/account/{number}/agency/{agency}/clientDocument/{clientDocument}/typeDocument/{typeDocument}/details",
                        "999", "999", "00000000000", "CPF"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenBalance_thenReturnsAccountBalance() throws Exception {
        var accountResponseDTO = AccountResponseDTO.builder()
                .clientName("John Doe")
                .accountNumber("000001")
                .agency("0001")
                .clientDocument("12345678901")
                .documentType(DocumentType.CPF)
                .balance(1000.0)
                .build();
        when(registerDataService.consultBalance(any(), any(), any(), any())).thenReturn(Double.valueOf("1000.00"));

        mockMvc.perform(get("/api/account/{number}/agency/{agency}/clientDocument/{clientDocument}/typeDocument/{typeDocument}/balance",
                        "123", "001", "12345678901", "CPF"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(accountResponseDTO.getBalance().toString()));
    }

    @Test
    void whenBalanceNotFound_thenReturnsNotFound() throws Exception {
        when(registerDataService.consultBalance(any(), any(), any(), any())).thenReturn(null);

        mockMvc.perform(get("/api/account/{number}/agency/{agency}/clientDocument/{clientDocument}/typeDocument/{typeDocument}/balance",
                        "999", "999", "00000000000", "CPF"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenTransfer_thenReturnsMessageResponse() throws Exception {
        var localMessageResponseDTO = MessageResponseDTO.builder()
                .id(UUID.randomUUID().toString())
                .description(UUID.randomUUID().toString())
                .status(MessageStatus.SUCCESS)
                .data(TransferResponseDTO.builder().transactionId(UUID.randomUUID().toString()).build())
                .build();

        var localTransferRequestDTO = TransferRequestDTO.builder()
                .transactionId(UUID.randomUUID().toString())
                .sourceAccount(AccountRequestDTO.builder()
                        .accountNumber("000001")
                        .agency("0001")
                        .clientDocument("12345678901")
                        .documentType(DocumentType.CPF)
                        .build())
                .targetAccount(AccountRequestDTO.builder()
                        .accountNumber("000001")
                        .agency("0001")
                        .clientDocument("12345678901")
                        .documentType(DocumentType.CPF)
                        .build())
                .value(100.0)
                .operationType(OperationType.TRANSFER)
                .build();

        when(cashOperationsService.transfer(any())).thenReturn(localMessageResponseDTO);

        mockMvc.perform(post("/api/account/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(localTransferRequestDTO))
                )
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(localMessageResponseDTO.getStatus().toString()));
    }
}
