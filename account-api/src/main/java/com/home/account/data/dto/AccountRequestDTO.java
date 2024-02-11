package com.home.account.data.dto;

import com.home.account.data.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDTO {

    @NotBlank(message = "Agency is required")
    private String agency;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Client document is required")
    private String clientDocument;

    @NotBlank(message = "Document type is required")
    private DocumentType documentType;
}
