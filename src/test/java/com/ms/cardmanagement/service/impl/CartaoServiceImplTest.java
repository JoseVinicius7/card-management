package com.ms.cardmanagement.service.impl;

import com.ms.cardmanagement.domain.CartaoEntity;
import com.ms.cardmanagement.domain.SituacaoCartao;
import com.ms.cardmanagement.domain.TipoCartao;
import com.ms.cardmanagement.dto.CartaoResponse;
import com.ms.cardmanagement.dto.CriarCartaoRequest;
import com.ms.cardmanagement.event.CartaoAtivadoEvent;
import com.ms.cardmanagement.event.CartaoCanceladoEvent;
import com.ms.cardmanagement.event.CartaoCriadoEvent;
import com.ms.cardmanagement.exception.CartaoException;
import com.ms.cardmanagement.mapper.CartaoMapper;
import com.ms.cardmanagement.producer.CartaoProducer;
import com.ms.cardmanagement.repository.CartaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartaoServiceImplTest {

    @InjectMocks
    private CartaoServiceImpl service;

    @Mock
    private CartaoRepository cartaoRepository;

    @Mock
    private CartaoProducer cartaoProducer;

    @Mock
    private CartaoMapper cartaoMapper;

    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarCartoes() {
        CriarCartaoRequest request = new CriarCartaoRequest("12345678900", "Nome Impresso", "Produto", "Subproduto");

        CartaoEntity cartaoFisico = buildCartaoEntity(TipoCartao.FISICO, SituacaoCartao.PENDENTE_ATIVACAO);
        CartaoEntity cartaoOnline = buildCartaoEntity(TipoCartao.ONLINE, SituacaoCartao.ATIVO);

        when(cartaoRepository.saveAll(anyList())).thenReturn(List.of(cartaoFisico, cartaoOnline));

        when(cartaoMapper.toCriadoEvent(any())).thenReturn(buildCartaoCriadoEvent());
        when(cartaoMapper.toResponse(any())).thenReturn(buildCartaoResponse());

        List<CartaoResponse> responses = service.criarCartoes(request);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(cartaoProducer, times(2)).publicarCartaoCriado(any());
    }

    @Test
    void deveAtivarCartao() {
        CartaoEntity cartao = buildCartaoEntity(TipoCartao.FISICO, SituacaoCartao.PENDENTE_ATIVACAO);

        when(cartaoRepository.findById(1L)).thenReturn(Optional.of(cartao));
        when(cartaoRepository.save(any())).thenReturn(cartao);
        when(cartaoMapper.toAtivadoEvent(any())).thenReturn(buildCartaoAtivadoEvent());
        when(cartaoMapper.toResponse(any())).thenReturn(buildCartaoResponse());

        CartaoResponse response = service.ativarCartao(1L);

        assertNotNull(response);
        verify(cartaoProducer).publicarCartaoAtivado(any());
    }

    @Test
    void deveLancarExcecaoQuandoCartaoNaoExiste() {
        when(cartaoRepository.findById(1L)).thenReturn(Optional.empty());

        CartaoException exception = assertThrows(CartaoException.class, () -> service.ativarCartao(1L));
        assertEquals("Cartão não encontrado para o ID: 1", exception.getMessage());
    }

    @Test
    void deveCancelarCartao() {
        CartaoEntity cartao = buildCartaoEntity(TipoCartao.FISICO, SituacaoCartao.ATIVO);

        when(cartaoRepository.findById(1L)).thenReturn(Optional.of(cartao));
        when(cartaoRepository.save(any())).thenReturn(cartao);

        service.cancelarCartao(1L);

        verify(cartaoProducer).publicarCartaoCancelado(any(CartaoCanceladoEvent.class));
    }

    @Test
    void deveLancarExcecaoAoCancelarCartaoNaoAtivo() {
        CartaoEntity cartao = buildCartaoEntity(TipoCartao.FISICO, SituacaoCartao.PENDENTE_ATIVACAO);
        when(cartaoRepository.findById(1L)).thenReturn(Optional.of(cartao));

        CartaoException exception = assertThrows(CartaoException.class, () -> service.cancelarCartao(1L));
        assertEquals("Somente cartões ATIVOS podem ser cancelados.", exception.getMessage());
    }

    // Métodos auxiliares

    private CartaoEntity buildCartaoEntity(TipoCartao tipo, SituacaoCartao situacao) {
        return CartaoEntity.builder()
                .id(1L)
                .cpf("12345678900")
                .nomeImpresso("Nome Impresso")
                .produto("Produto")
                .subproduto("Subproduto")
                .tipoCartao(tipo)
                .situacao(situacao)
                .dataCriacao(now)
                .dataAtualizacao(now)
                .dataAtivacao(now)
                .build();
    }

    private CartaoCriadoEvent buildCartaoCriadoEvent() {
        return new CartaoCriadoEvent(
                1L, "12345678900", "Nome Impresso",
                "Produto", "Subproduto",
                TipoCartao.FISICO.name(),
                SituacaoCartao.PENDENTE_ATIVACAO.name(),
                now
        );
    }

    private CartaoAtivadoEvent buildCartaoAtivadoEvent() {
        return new CartaoAtivadoEvent(
                1L, "12345678900", "Nome Impresso",
                "Produto", "Subproduto",
                TipoCartao.FISICO.name(),
                SituacaoCartao.ATIVO.name(),
                now.minusDays(1),
                now
        );
    }

    private CartaoResponse buildCartaoResponse() {
        return new CartaoResponse(
                1L, "12345678900", "Nome Impresso",
                "Produto", "Subproduto",
                TipoCartao.FISICO,
                SituacaoCartao.ATIVO,
                now
        );
    }
}
