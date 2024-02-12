package com.home.account.service.impl;

import com.home.account.data.dto.RegistrationResponseDTO;
import com.home.account.data.dto.TransferResponseDTO;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.model.Client;
import com.home.account.data.model.Version;
import com.home.account.integration.BacenClient;
import com.home.account.integration.PersonClient;
import com.home.account.repository.ClientRepository;
import com.home.account.repository.VersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ExternalIntegrationServiceImplTest {

    @Mock
    private PersonClient personClient;

    @Mock
    private BacenClient bacenClient;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private VersionRepository versionRepository;

    @Mock
    private KafkaTemplate<String, TransferResponseDTO> kafkaTemplate;

    private ExternalIntegrationServiceImpl externalIntegrationService;

    private final String document = "12345678901";
    private final String type = "CPF";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.externalIntegrationService = new ExternalIntegrationServiceImpl(personClient,
                bacenClient,
                clientRepository,
                versionRepository,
                kafkaTemplate);
    }

    @Test
    void whenFindClientDetailsAndClientExistsInCache_thenSuccess() {
        Client client = new Client(1L, document, DocumentType.valueOf(type), "John Doe", new Version());
        when(clientRepository.findByDocument(document)).thenReturn(client);

        RegistrationResponseDTO result = externalIntegrationService.findClientDetails(document, type);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void whenFindClientDetailsAndClientNotInCacheButPersonServiceSucceeds_thenSuccess() {
        when(clientRepository.findByDocument(document)).thenReturn(null);
        when(personClient.persons(document, type.toLowerCase())).thenReturn(new ResponseEntity<>(new RegistrationResponseDTO(document, "John Doe", type, null, false), HttpStatus.OK));
        when(versionRepository.findAllByActive("A")).thenReturn(new ArrayList<>() {
            {
                add(Version.builder().active("A").version("1.0").build());
            }
        });
        when(clientRepository.save(any())).thenReturn(new Client());


        RegistrationResponseDTO result = externalIntegrationService.findClientDetails(document, type);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void whenBacenRegisterSucceeds_thenTrue() {
       when(bacenClient.transferRegistration(any())).thenReturn(ResponseEntity.ok().build());

        boolean result = externalIntegrationService.bacenRegister(new TransferResponseDTO());

        assertTrue(result);
    }
}
