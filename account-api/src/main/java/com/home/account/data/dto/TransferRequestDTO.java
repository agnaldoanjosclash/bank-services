package com.home.account.data.dto;

import com.home.account.data.enums.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDTO {

    @NotNull(message = "Transaction ID is required")
    private String transactionId;

    @NotNull(message = "Source account is required")
    private AccountRequestDTO sourceAccount;

    @NotNull(message = "Target account is required")
    private AccountRequestDTO targetAccount;

    @NotNull(message = "Value is required")
    @Positive(message = "Value must be greater than 0")
    private Double value;

    @NotNull(message = "Operation type is required")
    private OperationType operationType;
}
