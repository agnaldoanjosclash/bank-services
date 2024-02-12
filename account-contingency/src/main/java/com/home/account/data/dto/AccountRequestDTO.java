package com.home.account.data.dto;

import com.home.account.data.enums.AccountStatus;
import com.home.account.data.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDTO {

    private String agency;

    private String accountNumber;

    private String clientName;

    private String clientDocument;

    private DocumentType documentType;

    private Double balance;

    private AccountStatus status;

    private String failMessage;

    @Builder.Default
    private Boolean failService = false;
}
