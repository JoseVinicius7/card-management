package com.ms.cardmanagement.producer;

import com.ms.cardmanagement.event.CartaoAtivadoEvent;
import com.ms.cardmanagement.event.CartaoCanceladoEvent;
import com.ms.cardmanagement.event.CartaoCriadoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class CartaoProducerTest {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private CartaoProducer cartaoProducer;

    @BeforeEach
    void setup() {
        kafkaTemplate = mock(KafkaTemplate.class);
        cartaoProducer = new CartaoProducer(kafkaTemplate);
    }

    @Test
    void devePublicarEventoCartaoCriadoNoTopicoCorreto() {
        CartaoCriadoEvent event = new CartaoCriadoEvent(1L, "12345678900", "Nome", "Produto", "Subproduto", "FISICO", "PENDENTE_ATIVACAO", null);

        cartaoProducer.publicarCartaoCriado(event);

        verify(kafkaTemplate, times(1)).send(eq("cartao.criado"), eq(event));
    }

    @Test
    void devePublicarEventoCartaoAtivadoNoTopicoCorreto() {
        CartaoAtivadoEvent event = new CartaoAtivadoEvent(1L, "12345678900", "Nome", "Produto", "Subproduto", "FISICO", "ATIVO", null, null);

        cartaoProducer.publicarCartaoAtivado(event);

        verify(kafkaTemplate, times(1)).send(eq("cartao.ativado"), eq(event));
    }

    @Test
    void devePublicarEventoCartaoCanceladoNoTopicoCorreto() {
        CartaoCanceladoEvent event = new CartaoCanceladoEvent(1L, "12345678900", "FISICO", null);

        cartaoProducer.publicarCartaoCancelado(event);

        verify(kafkaTemplate, times(1)).send(eq("cartao.cancelado"), eq(event));
    }
}
