package com.home.account.config;

import com.home.account.data.dto.TransferRequestDTO;
import com.home.account.data.dto.TransferResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfiguration {

    public static final String CONSUMER_GROUP = "cash_operation_group";
    public static final String CASH_OPERATION_TOPIC = "cash_operation_topic";
    public static final String BACEN_REGISTRATION_TOPIC = "bacen_registration_topic";
    private final String kafkaHost;

    public KafkaConfiguration(@Value("${client.kafka-host}") String kafkaHost) {
        this.kafkaHost = kafkaHost;
    }

    @Bean
    public ProducerFactory<String, TransferResponseDTO> producerFactory() {

        log.info("kafkaHost: {}", kafkaHost);

        Map<String, Object> config = new HashMap<String, Object>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<String, TransferResponseDTO>(config);
    }

    @Bean
    public KafkaTemplate<String, TransferResponseDTO> kafkaTemplate(ProducerFactory<String, TransferResponseDTO> producerFactory) {
        return new KafkaTemplate<String, TransferResponseDTO>(producerFactory());
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
