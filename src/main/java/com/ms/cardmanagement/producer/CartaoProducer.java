package com.ms.cardmanagement.producer;

import com.ms.cardmanagement.event.CartaoAtivadoEvent;
import com.ms.cardmanagement.event.CartaoCanceladoEvent;
import com.ms.cardmanagement.event.CartaoCriadoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartaoProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPICO_CARTAO_CRIADO = "cartao.criado";
    private static final String TOPICO_CARTAO_ATIVADO = "cartao.ativado";
    private static final String TOPICO_CARTAO_CANCELADO = "cartao.cancelado";


    public void publicarCartaoCriado(CartaoCriadoEvent event) {
        kafkaTemplate.send(TOPICO_CARTAO_CRIADO, event);
    }

    public void publicarCartaoAtivado(CartaoAtivadoEvent event) {
        kafkaTemplate.send(TOPICO_CARTAO_ATIVADO, event);
    }

    public void publicarCartaoCancelado(CartaoCanceladoEvent event) {
        kafkaTemplate.send(TOPICO_CARTAO_CANCELADO, event);
    }
}
