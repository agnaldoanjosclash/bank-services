package com.home.account.config;

import com.home.account.data.dto.TransferRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfiguration {

    public static final String CONSUMER_GROUP = "contingency_operation_group";
    public static final String BACEN_REGISTRATION_TOPIC = "bacen_registration_topic";
    private final String kafkaHost;

    public KafkaConfiguration(@Value("${client.kafka-host}") String kafkaHost) {
        this.kafkaHost = kafkaHost;
    }

    @Bean
    public ConsumerFactory<String, TransferRequestDTO> consumerFactory() {

        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaHost);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConfiguration.CONSUMER_GROUP);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<String, TransferRequestDTO>(config, new StringDeserializer(),
                new JsonDeserializer<TransferRequestDTO>(TransferRequestDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransferRequestDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransferRequestDTO> factory = new ConcurrentKafkaListenerContainerFactory<String, TransferRequestDTO>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
