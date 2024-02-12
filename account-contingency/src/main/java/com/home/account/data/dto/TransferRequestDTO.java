package com.home.account.data.dto;

import com.home.account.data.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDTO implements Serializable {

    private String transactionId;

    private AccountRequestDTO sourceAccount;

    private AccountRequestDTO targetAccount;

    private Double value;

    private OperationType operationType;

    private LocalDateTime operationDate;
}
