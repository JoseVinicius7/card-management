package com.ms.cardmanagement.producer;

import com.ms.cardmanagement.event.CartaoCriadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartaoProducer {

    private final KafkaTemplate<String, CartaoCriadoEvent> kafkaTemplate;

    private static final String TOPICO_CARTAO_CRIADO = "cartao.criado";

    public void publicar(CartaoCriadoEvent event) {
        kafkaTemplate.send(TOPICO_CARTAO_CRIADO, event);
    }
}
