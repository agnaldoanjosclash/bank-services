package com.home.account.service.impl;

import com.home.account.data.dto.AccountResponseDTO;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.model.Account;
import com.home.account.repository.AccountRepository;
import com.home.account.service.ExternalIntegrationService;
import com.home.account.service.RegisterDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterDataServiceImpl implements RegisterDataService {

    private final AccountRepository accountRepository;

    private final ExternalIntegrationService externalIntegrationService;

    @Override
    public AccountResponseDTO findAccountDetails(String number, String agency, String clientDocument, DocumentType documentType) {
        var sourceAccount = accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(
                agency,
                number,
                clientDocument,
                documentType
        );
        return  sourceAccount != null ? buildAccountDTO(sourceAccount) : null;
    }

    private AccountResponseDTO buildAccountDTO(final Account account) {
        var client = externalIntegrationService.findClientDetails(
                account.getClientDocument(),
                account.getDocumentType().toString()
        );

        if (client.getFailService()) {
            return AccountResponseDTO.builder()
                    .failMessage("System unavailable at the moment. Try again later.")
                    .failService(true)
                    .build();
        }

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

    @Override
    public Double consultBalance(String number, String agency, String clientDocument, DocumentType documentType) {

        var sourceAccount = accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(
                agency,
                number,
                clientDocument,
                documentType
        );

        return sourceAccount != null ? sourceAccount.getBalance() : null;
    }
}
