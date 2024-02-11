package com.home.account.service.impl;

import com.home.account.data.dto.AccountResponseDTO;
import com.home.account.data.dto.RegistrationResquestDTO;
import com.home.account.data.enums.AccountStatus;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.model.Account;
import com.home.account.repository.AccountRepository;
import com.home.account.service.ExternalIntegrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterDataServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ExternalIntegrationService externalIntegrationService;

    @InjectMocks
    private RegisterDataServiceImpl service;

    private Account account;
    private RegistrationResquestDTO registrationRequestDTO;

    @BeforeEach
    void setUp() {
        account = new Account(1L, "1234", "567890", "12345678901", DocumentType.CPF, 1000.0, AccountStatus.ACTIVE, 0.0, LocalDateTime.now());
        registrationRequestDTO = RegistrationResquestDTO.builder().name("John Doe").document("12345678901").documentType("CPF").failService(false).build();
    }

    @Test
    void whenFindAccountDetailsExists_thenSuccess() {
        when(accountRepository.findByAgencyAndAccountNumberAndClientDocumentAndTipoDocumento("1234", "567890", "12345678901", DocumentType.CPF)).thenReturn(account);
        when(externalIntegrationService.findClientDetails(any(), any())).thenReturn(registrationRequestDTO);

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
        registrationRequestDTO.setFailService(true);
        when(externalIntegrationService.findClientDetails(anyString(), anyString())).thenReturn(registrationRequestDTO);

        AccountResponseDTO result = service.findAccountDetails("567890", "1234", "12345678901", DocumentType.CPF);

        assertTrue(result.getFailService());
        assertEquals("System unavailable at the moment. Try again later.", result.getFailMessage());
    }
}
