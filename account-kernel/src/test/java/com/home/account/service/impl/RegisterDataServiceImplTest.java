package com.home.account.service.impl;

import com.home.account.data.dto.AccountResponseDTO;
import com.home.account.data.dto.RegistrationResponseDTO;
import com.home.account.data.enums.AccountStatus;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.model.Account;
import com.home.account.repository.AccountRepository;
import com.home.account.service.ExternalIntegrationService;
import com.home.account.service.RegisterDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class RegisterDataServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ExternalIntegrationService externalIntegrationService;

    private RegisterDataService service;

    private Account account;
    private RegistrationResponseDTO registrationResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        this.service = new RegisterDataServiceImpl(accountRepository, externalIntegrationService);

        account = new Account(1L, "1234", "567890", "12345678901", DocumentType.CPF, 1000.0, AccountStatus.ACTIVE, 0.0, LocalDateTime.now());
        registrationResponseDTO = RegistrationResponseDTO.builder().name("John Doe").document("12345678901").documentType("CPF").build();
    }

    @Test
    void whenFindAccountDetailsExists_thenSuccess() {
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento("1234", "567890", "12345678901", DocumentType.CPF)).thenReturn(account);
        when(externalIntegrationService.findClientDetails(any(), any())).thenReturn(registrationResponseDTO);

        AccountResponseDTO result = service.findAccountDetails("567890", "1234", "12345678901", DocumentType.CPF);

        assertNotNull(result);
        assertEquals("John Doe", result.getClientName());
    }

    @Test
    void whenFindAccountDetailsNotExists_thenNull() {
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento("1234", "567890", "12345678901", DocumentType.CPF)).thenReturn(null);

        AccountResponseDTO result = service.findAccountDetails("567890", "1234", "12345678901", DocumentType.CPF);

        assertNull(result);
    }

    @Test
    void whenConsultBalanceExists_thenSuccess() {
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento("1234", "567890", "12345678901", DocumentType.CPF)).thenReturn(account);

        Double balance = service.consultBalance("567890", "1234", "12345678901", DocumentType.CPF);

        assertNotNull(balance);
        assertEquals(1000.0, balance);
    }

    @Test
    void whenConsultBalanceNotExists_thenNull() {
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento("1234", "567890", "12345678901", DocumentType.CPF)).thenReturn(null);

        Double balance = service.consultBalance("567890", "1234", "12345678901", DocumentType.CPF);

        assertNull(balance);
    }

    @Test
    void whenExternalServiceFails_thenFailMessage() {
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento(anyString(), anyString(), anyString(), any())).thenReturn(account);
        registrationResponseDTO.setFailService(true);
        when(externalIntegrationService.findClientDetails(anyString(), anyString())).thenReturn(registrationResponseDTO);

        AccountResponseDTO result = service.findAccountDetails("567890", "1234", "12345678901", DocumentType.CPF);

        assertTrue(result.getFailService());
        assertEquals("System unavailable at the moment. Try again later.", result.getFailMessage());
    }
}
