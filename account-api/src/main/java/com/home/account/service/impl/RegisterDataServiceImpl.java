package com.home.account.service.impl;

import com.home.account.data.dto.AccountResponseDTO;
import com.home.account.data.enums.DocumentType;
import com.home.account.integration.AccountKernelClient;
import com.home.account.service.RegisterDataService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterDataServiceImpl implements RegisterDataService {

    private final AccountKernelClient accountKernelClient;

    @Override
    @CircuitBreaker(name="detail-service", fallbackMethod = "findAccountDetailsFallback")
    public AccountResponseDTO findAccountDetails(String number, String agency, String clientDocument, DocumentType documentType) {
        var sourceAccount = accountKernelClient.details(
                number,
                agency,
                clientDocument,
                documentType.toString()
        );
        return  sourceAccount.getBody();
    }

    public AccountResponseDTO findAccountDetailsFallback(Exception e) {
        log.error("Errors occurred during execution.", e);
        return AccountResponseDTO.builder()
                .failMessage("System unavailable at the moment. Try again later.")
                .failService(true)
                .build();
    }

    @Override
    public Double consultBalance(String number, String agency, String clientDocument, DocumentType documentType) {

        return accountKernelClient.balance(
                number,
                agency,
                clientDocument,
                documentType.toString()
        ).getBody();
    }
}
