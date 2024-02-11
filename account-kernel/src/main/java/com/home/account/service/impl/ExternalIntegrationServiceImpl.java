package com.home.account.service.impl;

import com.home.account.config.CacheConfiguration;
import com.home.account.config.KafkaConfiguration;
import com.home.account.data.dto.RegistrationResquestDTO;
import com.home.account.data.dto.TransferResponseDTO;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.model.Client;
import com.home.account.integration.BacenClient;
import com.home.account.integration.PersonClient;
import com.home.account.repository.ClientRepository;
import com.home.account.repository.VersionRepository;
import com.home.account.service.ExternalIntegrationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalIntegrationServiceImpl implements ExternalIntegrationService {

    private final PersonClient personClient;

    private final BacenClient bacenClient;

    private final ClientRepository clientRepository;

    private final VersionRepository versionRepository;

    private final KafkaTemplate<String, TransferResponseDTO> kafkaTemplate;

    @Override
    @Cacheable(value = CacheConfiguration.CLIENT_CACHE , key="{#document, #type}")
    @CircuitBreaker(name="registry-service", fallbackMethod = "fallbackMethod")
    public RegistrationResquestDTO findClientDetails(String document, String type) {

        var clientCache =  clientRepository.findByDocument(document);

        if (clientCache != null) {
            return clientCache.toDTO();
        }

        ResponseEntity<RegistrationResquestDTO> personResponse = personClient.persons(document, type.toLowerCase());
        if (!personResponse.getStatusCode().is2xxSuccessful()) {
            return null;
        }

        RegistrationResquestDTO registrationResquestDTO = personResponse.getBody();
        if (registrationResquestDTO != null) {
            saveClientCache(registrationResquestDTO);
        }

        return registrationResquestDTO;
    }

    @Override
    public boolean bacenRegister(TransferResponseDTO transferResponseDTO) {
        try {
            bacenClient.transferRegistration(transferResponseDTO);
        } catch (Exception e) {
            log.error("Error occurred during registration in BACEN.", e);
            log.error("Transaction Id {}", transferResponseDTO.getTransactionId());
            kafkaTemplate.send(KafkaConfiguration.BACEN_REGISTRATION_TOPIC, transferResponseDTO);
            return false;
        }
        return true;
    }

    private void saveClientCache(RegistrationResquestDTO registrationResquestDTO) {
        var versionCache = versionRepository.findAllByActive("A").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Active version not found"));

        Client client = Client.builder()
                .name(registrationResquestDTO.getName())
                .document(registrationResquestDTO.getDocument())
                .documentType(DocumentType.valueOf(registrationResquestDTO.getDocumentType().toUpperCase()))
                .version(versionCache)
                .build();

        clientRepository.save(client);
    }

    public RegistrationResquestDTO fallbackMethod(Exception e) {
        log.error("Errors occurred during execution.", e);
        return RegistrationResquestDTO.builder()
                .name("Service Unavailable")
                .document("00000000000")
                .documentType("")
                .failMessage("System unavailable at the moment. Try again later.")
                .failService(true)
                .build();
    }
}
