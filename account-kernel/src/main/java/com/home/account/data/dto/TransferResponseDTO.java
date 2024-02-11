package com.home.account.data.dto;

import com.home.account.data.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponseDTO {

    private String transactionId;

    private AccountResponseDTO sourceAccount;

    private AccountResponseDTO targetAccount;

    private Double value;

    private OperationType operationType;

    private LocalDateTime operationDate;
}
