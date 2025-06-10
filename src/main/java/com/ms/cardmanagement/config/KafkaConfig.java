package com.ms.cardmanagement.config;

import com.ms.cardmanagement.event.CartaoAtivadoEvent;
import com.ms.cardmanagement.event.CartaoCanceladoEvent;
import com.ms.cardmanagement.event.CartaoCriadoEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, CartaoCriadoEvent> cartaoCriadoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, CartaoAtivadoEvent> cartaoAtivadoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<String, CartaoCanceladoEvent> cartaoCanceladoProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, CartaoCriadoEvent> cartaoCriadoKafkaTemplate(
            ProducerFactory<String, CartaoCriadoEvent> cartaoCriadoProducerFactory) {
        return new KafkaTemplate<>(cartaoCriadoProducerFactory);
    }

    @Bean
    public KafkaTemplate<String, CartaoAtivadoEvent> cartaoAtivadoKafkaTemplate(
            ProducerFactory<String, CartaoAtivadoEvent> cartaoAtivadoProducerFactory) {
        return new KafkaTemplate<>(cartaoAtivadoProducerFactory);
    }

    @Bean
    public KafkaTemplate<String, CartaoCanceladoEvent> cartaoCanceladoKafkaTemplate(
            ProducerFactory<String, CartaoCanceladoEvent> cartaoCanceladoProducerFactory) {
        return new KafkaTemplate<>(cartaoCanceladoProducerFactory);
    }
}