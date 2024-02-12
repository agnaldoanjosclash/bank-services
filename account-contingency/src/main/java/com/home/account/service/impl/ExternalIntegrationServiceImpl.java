package com.home.account.service.impl;

import com.home.account.config.KafkaConfiguration;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.integration.BacenClient;
import com.home.account.service.ExternalIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalIntegrationServiceImpl implements ExternalIntegrationService {

    private final BacenClient bacenClient;

    private final KafkaTemplate<String, TransferRequestDTO> kafkaTemplate;

    @Override
    public boolean bacenRegister(TransferRequestDTO transferResponseDTO) {
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
}
