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
        log.info("Iniciando criação de cartões para CPF: {}", request.getCpf());

        var agora = LocalDateTime.now();
        var cartaoFisico = criarCartaoEntity(request, TipoCartao.FISICO, SituacaoCartao.PENDENTE_ATIVACAO, agora);
        var cartaoOnline = criarCartaoEntity(request, TipoCartao.ONLINE, SituacaoCartao.ATIVO, agora);

        var cartoesSalvos = cartaoRepository.saveAll(List.of(cartaoFisico, cartaoOnline));
        log.info("Cartões salvos com sucesso no banco. Quantidade: {}", cartoesSalvos.size());

        cartoesSalvos.forEach(this::publicarCartaoCriadoEvent);

        return cartoesSalvos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CartaoEntity criarCartaoEntity(CriarCartaoRequest request, TipoCartao tipo, SituacaoCartao situacao, LocalDateTime dataCriacao) {
        CartaoEntity cartao = new CartaoEntity();
        cartao.setCpf(request.getCpf());
        cartao.setNomeImpresso(request.getNomeImpresso());
        cartao.setProduto(request.getProduto());
        cartao.setSubproduto(request.getSubproduto());
        cartao.setTipoCartao(tipo);
        cartao.setSituacao(situacao);
        cartao.setDataCriacao(dataCriacao);
        return cartao;
    }

    private void publicarCartaoCriadoEvent(CartaoEntity cartaoEntity) {
        CartaoCriadoEvent event = toEvent(cartaoEntity);
        cartaoProducer.publicarCartaoCriado(event);
        log.info("Evento publicado para cartão ID {} - Tipo: {}", cartaoEntity.getId(), cartaoEntity.getTipoCartao());
    }

    @Override
    public CartaoResponse ativarCartao(Long id) {
        log.info("Iniciando ativação do cartão físico ID: {}", id);
        CartaoEntity cartaoEntity = buscarCartaoOuFalhar(id);

        validarCartaoParaAtivacao(cartaoEntity);

        cartaoEntity.setSituacao(SituacaoCartao.ATIVO);
        var agora = LocalDateTime.now();
        cartaoEntity.setDataAtualizacao(agora);

        CartaoEntity cartaoAtivado = cartaoRepository.save(cartaoEntity);
        log.info("Cartão físico ID: {} ativado com sucesso.", id);

        CartaoAtivadoEvent event = toAtivadoEvent(cartaoAtivado);
        cartaoProducer.publicarCartaoAtivado(event);
        log.info("Evento CartaoAtivadoEvent publicado para cartão ID: {}", id);

        return toResponse(cartaoAtivado);
    }

    private CartaoEntity buscarCartaoOuFalhar(Long id) {
        return cartaoRepository.findById(id)
                .orElseThrow(() -> new CartaoException("Cartão não encontrado para o ID: " + id));
    }

    private void validarCartaoParaAtivacao(CartaoEntity cartao) {
        if (cartao.getTipoCartao() != TipoCartao.FISICO) {
            log.warn("Tentativa de ativar cartão que não é físico. ID: {}, Tipo: {}", cartao.getId(), cartao.getTipoCartao());
            throw new CartaoException("Só é permitido ativar cartão do tipo FÍSICO.");
        }

        if (cartao.getSituacao() != SituacaoCartao.PENDENTE_ATIVACAO) {
            log.warn("Tentativa de ativar cartão que não está pendente. ID: {}, Situação: {}", cartao.getId(), cartao.getSituacao());
            throw new CartaoException("Cartão deve estar com status PENDENTE_ATIVACAO para ativação.");
        }
    }

    @Override
    public void cancelarCartao(Long id) {
        CartaoEntity cartao = buscarCartaoOuFalhar(id);

        if (cartao.getSituacao() != SituacaoCartao.ATIVO) {
            throw new CartaoException("Somente cartões ATIVOS podem ser cancelados.");
        }

        cartao.setSituacao(SituacaoCartao.CANCELADO);
        cartao.setDataAtualizacao(LocalDateTime.now());
        cartaoRepository.save(cartao);

        var event = new CartaoCanceladoEvent(
                cartao.getId(),
                cartao.getCpf(),
                cartao.getTipoCartao().name(),
                LocalDateTime.now()
        );

        cartaoProducer.publicarCartaoCancelado(event);
        log.info("Cartão ID {} cancelado com sucesso", cartao.getId());
    }

    private CartaoResponse toResponse(CartaoEntity cartaoEntity) {
        return new CartaoResponse(
                cartaoEntity.getId(),
                cartaoEntity.getCpf(),
                cartaoEntity.getNomeImpresso(),
                cartaoEntity.getProduto(),
                cartaoEntity.getSubproduto(),
                cartaoEntity.getTipoCartao(),
                cartaoEntity.getSituacao(),
                cartaoEntity.getDataCriacao()
        );
    }


    private CartaoCriadoEvent toEvent(CartaoEntity cartaoEntity) {
        return new CartaoCriadoEvent(
                cartaoEntity.getId(),
                cartaoEntity.getCpf(),
                cartaoEntity.getNomeImpresso(),
                cartaoEntity.getProduto(),
                cartaoEntity.getSubproduto(),
                cartaoEntity.getTipoCartao().name(),
                cartaoEntity.getSituacao().name(),
                cartaoEntity.getDataCriacao()
        );
    }

    private CartaoAtivadoEvent toAtivadoEvent(CartaoEntity cartaoEntity) {
        return new CartaoAtivadoEvent(
                cartaoEntity.getId(),
                cartaoEntity.getCpf(),
                cartaoEntity.getNomeImpresso(),
                cartaoEntity.getProduto(),
                cartaoEntity.getSubproduto(),
                cartaoEntity.getTipoCartao().name(),
                cartaoEntity.getSituacao().name(),
                cartaoEntity.getDataCriacao(),
                cartaoEntity.getDataAtualizacao()
        );
    }
}
