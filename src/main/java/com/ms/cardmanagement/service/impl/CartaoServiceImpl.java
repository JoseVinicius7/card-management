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
import com.ms.cardmanagement.producer.CartaoProducer;
import com.ms.cardmanagement.repository.CartaoRepository;
import com.ms.cardmanagement.service.CartaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartaoServiceImpl implements CartaoService {

    private final CartaoRepository cartaoRepository;
    private final CartaoProducer cartaoProducer;

    @Override
    public List<CartaoResponse> criarCartoes(CriarCartaoRequest request) {
        log.info("Criando cartões para CPF: {}", request.getCpf());

        CartaoEntity cartaoFisico = criarCartao(request, TipoCartao.FISICO, SituacaoCartao.PENDENTE_ATIVACAO);
        CartaoEntity cartaoOnline = criarCartao(request, TipoCartao.ONLINE, SituacaoCartao.ATIVO);

        List<CartaoEntity> cartoesSalvos = cartaoRepository.saveAll(List.of(cartaoFisico, cartaoOnline));
        log.info("Cartões salvos: {}", cartoesSalvos.size());

        cartoesSalvos.forEach(cartao -> {
            cartaoProducer.publicarCartaoCriado(toEvent(cartao));
            log.info("Evento CartaoCriadoEvent publicado - ID: {}, Tipo: {}", cartao.getId(), cartao.getTipoCartao());
        });

        return cartoesSalvos.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public CartaoResponse ativarCartao(Long id) {
        log.info("Ativando cartão físico ID: {}", id);
        CartaoEntity cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new CartaoException("Cartão não encontrado - ID: " + id));

        if (cartao.getTipoCartao() != TipoCartao.FISICO) {
            throw new CartaoException("Apenas cartões FÍSICOS podem ser ativados.");
        }

        if (cartao.getSituacao() != SituacaoCartao.PENDENTE_ATIVACAO) {
            throw new CartaoException("Cartão deve estar com status PENDENTE_ATIVACAO.");
        }

        cartao.setSituacao(SituacaoCartao.ATIVO);
        cartao.setDataAtualizacao(LocalDateTime.now());
        CartaoEntity atualizado = cartaoRepository.save(cartao);

        cartaoProducer.publicarCartaoAtivado(toAtivadoEvent(atualizado));
        log.info("Cartão ativado com sucesso - ID: {}", id);

        return toResponse(atualizado);
    }

    @Override
    public void cancelarCartao(Long id) {
        log.info("Cancelando cartão ID: {}", id);
        CartaoEntity cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new CartaoException("Cartão não encontrado - ID: " + id));

        if (cartao.getSituacao() != SituacaoCartao.ATIVO) {
            throw new CartaoException("Somente cartões ATIVOS podem ser cancelados.");
        }

        cartao.setSituacao(SituacaoCartao.CANCELADO);
        cartao.setDataAtualizacao(LocalDateTime.now());
        cartaoRepository.save(cartao);

        cartaoProducer.publicarCartaoCancelado(toCanceladoEvent(cartao));
        log.info("Cartão cancelado com sucesso - ID: {}", id);
    }

    // -------- Métodos auxiliares --------

    private CartaoEntity criarCartao(CriarCartaoRequest request, TipoCartao tipo, SituacaoCartao situacao) {
        CartaoEntity cartao = new CartaoEntity();
        cartao.setCpf(request.getCpf());
        cartao.setNomeImpresso(request.getNomeImpresso());
        cartao.setProduto(request.getProduto());
        cartao.setSubproduto(request.getSubproduto());
        cartao.setTipoCartao(tipo);
        cartao.setSituacao(situacao);
        cartao.setDataCriacao(LocalDateTime.now());
        return cartao;
    }

    private CartaoResponse toResponse(CartaoEntity cartao) {
        return CartaoResponse.builder()
                .id(cartao.getId())
                .cpf(cartao.getCpf())
                .nomeImpresso(cartao.getNomeImpresso())
                .produto(cartao.getProduto())
                .subproduto(cartao.getSubproduto())
                .tipoCartao(cartao.getTipoCartao())
                .situacao(cartao.getSituacao())
                .dataCriacao(cartao.getDataCriacao())
                .build();
    }

    private CartaoCriadoEvent toEvent(CartaoEntity cartao) {
        return new CartaoCriadoEvent(
                cartao.getId(),
                cartao.getCpf(),
                cartao.getNomeImpresso(),
                cartao.getProduto(),
                cartao.getSubproduto(),
                cartao.getTipoCartao().name(),
                cartao.getSituacao().name(),
                cartao.getDataCriacao()
        );
    }

    private CartaoAtivadoEvent toAtivadoEvent(CartaoEntity cartao) {
        return new CartaoAtivadoEvent(
                cartao.getId(),
                cartao.getCpf(),
                cartao.getNomeImpresso(),
                cartao.getProduto(),
                cartao.getSubproduto(),
                cartao.getTipoCartao().name(),
                cartao.getSituacao().name(),
                cartao.getDataCriacao(),
                cartao.getDataAtualizacao()
        );
    }

    private CartaoCanceladoEvent toCanceladoEvent(CartaoEntity cartao) {
        return new CartaoCanceladoEvent(
                cartao.getId(),
                cartao.getCpf(),
                cartao.getTipoCartao().name(),
                cartao.getDataAtualizacao()
        );
    }
}
