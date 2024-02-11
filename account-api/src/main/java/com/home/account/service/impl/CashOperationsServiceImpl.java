package com.home.account.service.impl;

import com.home.account.config.KafkaConfiguration;
import com.home.account.data.dto.MessageResponseDTO;
import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.data.enums.MessageStatus;
import com.home.account.integration.AccountKernelClient;
import com.home.account.service.CashOperationsService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CashOperationsServiceImpl implements CashOperationsService {

    public static final int TOTAL_PARTITIONS = 3;
    public static final int TOTAL_INTERACTIONS = 11;

    private final KafkaTemplate<String, TransferRequestDTO> kafkaTemplate;

    private final AccountKernelClient accountKernelClient;

    @Override
    public MessageResponseDTO transfer(TransferRequestDTO transferRequestDTO) {

        Long partition = Long.valueOf(transferRequestDTO.getSourceAccount().getClientDocument()) % TOTAL_PARTITIONS;

        var key = String.format("%s:%s",
                transferRequestDTO.getSourceAccount().getClientDocument(),
                transferRequestDTO.getSourceAccount().getDocumentType()
        );

        log.info("Sending transfer to kafka {}", transferRequestDTO);

        kafkaTemplate.send(KafkaConfiguration.CASH_OPERATION_TOPIC, partition.intValue(), key, transferRequestDTO);

        for (int i = 0; i < TOTAL_INTERACTIONS; i++) {
            try {
                ResponseEntity<MessageResponseDTO> message = accountKernelClient.transaction(
                        transferRequestDTO.getTransactionId());
                if (message.getStatusCode().is2xxSuccessful()) {
                    MessageResponseDTO response = message.getBody();
                    response.setData(transferRequestDTO);
                    return response;
                }
                Thread.sleep(3);
            } catch (InterruptedException | FeignException.NotFound e) {
                log.error("Making a new interaction: {}", e.getMessage());
            }
        }

        return MessageResponseDTO.builder()
                .id(UUID.randomUUID().toString())
                .description("Transfer timeout, try again later.")
                .status(MessageStatus.FAIL)
                .data(transferRequestDTO)
                .build();
    }

}
