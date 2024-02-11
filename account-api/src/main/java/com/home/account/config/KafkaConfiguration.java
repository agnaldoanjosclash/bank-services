package com.home.account.config;

import com.home.account.data.dto.TransferRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfiguration {

    public static final String CONSUMER_GROUP = "cash_operation_group";
    public static final String CASH_OPERATION_TOPIC = "cash_operation_topic";
    private final String kafkaHost;

    public KafkaConfiguration(@Value("${client.kafka-host}") String kafkaHost) {
        this.kafkaHost = kafkaHost;
    }

    @Bean
    public ProducerFactory<String, TransferRequestDTO> producerFactory() {

        log.info("kafkaHost: {}", kafkaHost);

        Map<String, Object> config = new HashMap<String, Object>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<String, TransferRequestDTO>(config);
    }

    @Bean
    public KafkaTemplate<String, TransferRequestDTO> kafkaTemplate(ProducerFactory<String, TransferRequestDTO> producerFactory) {
        return new KafkaTemplate<String, TransferRequestDTO>(producerFactory());
    }

}
