package com.home.account.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {

    private String transactionId;

    private LocalDateTime transactionDateTime;

    private String description;

    private String success;

}
