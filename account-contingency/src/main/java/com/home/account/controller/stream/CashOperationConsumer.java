package com.home.account.controller.stream;

import com.home.account.config.KafkaConfiguration;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.service.ExternalIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CashOperationConsumer {

    private final ExternalIntegrationService externalIntegrationService;

    @KafkaListener(topics = KafkaConfiguration.BACEN_REGISTRATION_TOPIC,
            groupId = KafkaConfiguration.CONSUMER_GROUP,
            containerFactory="kafkaListenerContainerFactory")
    public void consume(final TransferRequestDTO transferRequestDTO) {
        log.info("Consuming transfer request: {}", transferRequestDTO);
        externalIntegrationService.bacenRegister(transferRequestDTO);
    }



}
