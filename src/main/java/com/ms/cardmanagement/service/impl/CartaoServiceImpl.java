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
import com.ms.cardmanagement.exception.ErrorCode;
import com.ms.cardmanagement.mapper.CartaoMapper;
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
    private final CartaoMapper cartaoMapper;

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
                .map(cartaoMapper::toResponse)
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
        CartaoCriadoEvent event = cartaoMapper.toCriadoEvent(cartaoEntity);
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

        CartaoAtivadoEvent event = cartaoMapper.toAtivadoEvent(cartaoEntity);
        cartaoProducer.publicarCartaoAtivado(event);
        log.info("Evento CartaoAtivadoEvent publicado para cartão ID: {}", id);

        return cartaoMapper.toResponse(cartaoAtivado);
    }

    private CartaoEntity buscarCartaoOuFalhar(Long id) {
        return cartaoRepository.findById(id)
                .orElseThrow(() -> new CartaoException(ErrorCode.CARTAO_NAO_ENCONTRADO));
    }

    private void validarCartaoParaAtivacao(CartaoEntity cartao) {
        if (cartao.getTipoCartao() != TipoCartao.FISICO) {
            log.warn("Tentativa de ativar cartão que não é físico. ID: {}, Tipo: {}", cartao.getId(), cartao.getTipoCartao());
            throw new CartaoException(ErrorCode.ATIVACAO_INVALIDA);
        }

        if (cartao.getSituacao() != SituacaoCartao.PENDENTE_ATIVACAO) {
            log.warn("Tentativa de ativar cartão que não está pendente. ID: {}, Situação: {}", cartao.getId(), cartao.getSituacao());
            throw new CartaoException(ErrorCode.ATIVACAO_INVALIDA);
        }
    }

    @Override
    public void cancelarCartao(Long id) {
        CartaoEntity cartao = buscarCartaoOuFalhar(id);

        if (cartao.getSituacao() != SituacaoCartao.ATIVO) {
            throw new CartaoException(ErrorCode.CANCELAMENTO_INVALIDO);
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

}
