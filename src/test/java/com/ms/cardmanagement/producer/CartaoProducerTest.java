package com.ms.cardmanagement.producer;

import com.ms.cardmanagement.event.CartaoAtivadoEvent;
import com.ms.cardmanagement.event.CartaoCanceladoEvent;
import com.ms.cardmanagement.event.CartaoCriadoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class CartaoProducerTest {

    private KafkaTemplate<String, CartaoCriadoEvent> cartaoCriadoKafkaTemplate;
    private KafkaTemplate<String, CartaoAtivadoEvent> cartaoAtivadoKafkaTemplate;
    private KafkaTemplate<String, CartaoCanceladoEvent> cartaoCanceladoKafkaTemplate;
    private CartaoProducer cartaoProducer;

    @BeforeEach
    void setup() {
        cartaoCriadoKafkaTemplate = mock(KafkaTemplate.class);
        cartaoAtivadoKafkaTemplate = mock(KafkaTemplate.class);
        cartaoCanceladoKafkaTemplate = mock(KafkaTemplate.class);
        cartaoProducer = new CartaoProducer(
            cartaoCriadoKafkaTemplate,
            cartaoAtivadoKafkaTemplate,
            cartaoCanceladoKafkaTemplate
        );
    }

    @Test
    void devePublicarEventoCartaoCriadoNoTopicoCorreto() {
        CartaoCriadoEvent event = new CartaoCriadoEvent(1L, "12345678900", "Nome", "Produto", "Subproduto", "FISICO", "PENDENTE_ATIVACAO", null);

        cartaoProducer.publicarCartaoCriado(event);

        verify(cartaoCriadoKafkaTemplate, times(1)).send(eq("cartao.criado"), eq(event));
        verifyNoInteractions(cartaoAtivadoKafkaTemplate, cartaoCanceladoKafkaTemplate);
    }

    @Test
    void devePublicarEventoCartaoAtivadoNoTopicoCorreto() {
        CartaoAtivadoEvent event = new CartaoAtivadoEvent(1L, "12345678900", "Nome", "Produto", "Subproduto", "FISICO", "ATIVO", null, null);

        cartaoProducer.publicarCartaoAtivado(event);

        verify(cartaoAtivadoKafkaTemplate, times(1)).send(eq("cartao.ativado"), eq(event));
        verifyNoInteractions(cartaoCriadoKafkaTemplate, cartaoCanceladoKafkaTemplate);
    }

    @Test
    void devePublicarEventoCartaoCanceladoNoTopicoCorreto() {
        CartaoCanceladoEvent event = new CartaoCanceladoEvent(1L, "12345678900", "FISICO", null);

        cartaoProducer.publicarCartaoCancelado(event);

        verify(cartaoCanceladoKafkaTemplate, times(1)).send(eq("cartao.cancelado"), eq(event));
        verifyNoInteractions(cartaoCriadoKafkaTemplate, cartaoAtivadoKafkaTemplate);
    }
}