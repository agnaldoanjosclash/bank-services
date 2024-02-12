package com.home.account.service.impl;

import com.home.account.config.CacheConfiguration;
import com.home.account.config.KafkaConfiguration;
import com.home.account.data.dto.RegistrationResponseDTO;
import com.home.account.data.dto.TransferResponseDTO;
import com.home.account.data.enums.DocumentType;
import com.home.account.data.model.Client;
import com.home.account.integration.BacenClient;
import com.home.account.integration.PersonClient;
import com.home.account.repository.ClientRepository;
import com.home.account.repository.VersionRepository;
import com.home.account.service.ExternalIntegrationService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
    @CircuitBreaker(name="registry-circuit-breaker", fallbackMethod = "fallbackMethod")
    public RegistrationResponseDTO findClientDetails(String document, String type) {

        var clientCache =  clientRepository.findByDocument(document);

        if (clientCache != null) {
            return clientCache.toDTO();
        }

        ResponseEntity<RegistrationResponseDTO> personResponse = personClient.persons(document, type.toLowerCase());
        if (!personResponse.getStatusCode().is2xxSuccessful()) {
            return null;
        }

        RegistrationResponseDTO registrationResponseDTO = personResponse.getBody();
        if (registrationResponseDTO != null) {
            saveClientCache(registrationResponseDTO);
        }

        return registrationResponseDTO;
    }

    @Override
    @CircuitBreaker(name="bacen-circuit-breaker", fallbackMethod = "bacenCircuitBreakerFallbackMethod")
    @RateLimiter(name = "bacen-rate-limiter", fallbackMethod = "bacenRateLimiterFallbackMethod")
    public boolean bacenRegister(TransferResponseDTO transferResponseDTO) {

        bacenClient.transferRegistration(transferResponseDTO);

        return true;
    }

    private void saveClientCache(RegistrationResponseDTO registrationResponseDTO) {
        var versionCache = versionRepository.findAllByActive("A").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Active version not found"));

        Client client = Client.builder()
                .name(registrationResponseDTO.getName())
                .document(registrationResponseDTO.getDocument())
                .documentType(DocumentType.valueOf(registrationResponseDTO.getDocumentType().toUpperCase()))
                .version(versionCache)
                .build();

        clientRepository.save(client);
    }

    public RegistrationResponseDTO fallbackMethod(Exception e) {
        log.error("Errors occurred during execution.", e);
        return RegistrationResponseDTO.builder()
                .name("Service Unavailable")
                .document("00000000000")
                .documentType("")
                .failMessage("System unavailable at the moment. Try again later.")
                .failService(true)
                .build();
    }

    public boolean bacenRateLimiterFallbackMethod(TransferResponseDTO transferResponseDTO, RequestNotPermitted e) {
        log.error("Não foi possível notificar o BACEN devido a limitação de taxa. Sua transação será reprocessada.", e);
        log.error("Transaction Id {}", transferResponseDTO.getTransactionId());
        kafkaTemplate.send(KafkaConfiguration.BACEN_REGISTRATION_TOPIC, transferResponseDTO);
        return false;
    }

    public boolean bacenCircuitBreakerFallbackMethod(TransferResponseDTO transferResponseDTO, Exception e) {
        log.error("Não foi possível notificar o BACEN devido a uma falha no serviço. Sua transação será reprocessada.", e);
        log.error("Transaction Id {}", transferResponseDTO.getTransactionId());
        kafkaTemplate.send(KafkaConfiguration.BACEN_REGISTRATION_TOPIC, transferResponseDTO);
        return false;
    }
}
