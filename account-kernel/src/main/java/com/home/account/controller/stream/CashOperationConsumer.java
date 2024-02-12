package com.home.account.controller.stream;

import com.home.account.config.KafkaConfiguration;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.service.CashOperationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CashOperationConsumer {

    private final CashOperationsService cashOperationsService;

    @KafkaListener(topics = KafkaConfiguration.CASH_OPERATION_TOPIC,
            groupId = KafkaConfiguration.CONSUMER_GROUP,
            containerFactory="kafkaListenerContainerFactory")
    public void consume(final TransferRequestDTO transferRequestDTO) {
        log.info("Consuming message from topic: {}", transferRequestDTO);
        cashOperationsService.transfer(transferRequestDTO);
    }



}
