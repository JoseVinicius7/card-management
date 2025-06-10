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

    private final KafkaTemplate<String, CartaoCriadoEvent> cartaoCriadoKafkaTemplate;
    private final KafkaTemplate<String, CartaoAtivadoEvent> cartaoAtivadoKafkaTemplate;
    private final KafkaTemplate<String, CartaoCanceladoEvent> cartaoCanceladoKafkaTemplate;

    private static final String TOPICO_CARTAO_CRIADO = "cartao.criado";
    private static final String TOPICO_CARTAO_ATIVADO = "cartao.ativado";
    private static final String TOPICO_CARTAO_CANCELADO = "cartao.cancelado";

    public void publicarCartaoCriado(CartaoCriadoEvent event) {
        cartaoCriadoKafkaTemplate.send(TOPICO_CARTAO_CRIADO, event);
    }

    public void publicarCartaoAtivado(CartaoAtivadoEvent event) {
        cartaoAtivadoKafkaTemplate.send(TOPICO_CARTAO_ATIVADO, event);
    }

    public void publicarCartaoCancelado(CartaoCanceladoEvent event) {
        cartaoCanceladoKafkaTemplate.send(TOPICO_CARTAO_CANCELADO, event);
    }
}