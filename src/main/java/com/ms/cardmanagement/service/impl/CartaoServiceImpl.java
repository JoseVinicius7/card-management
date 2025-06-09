package com.ms.cardmanagement.service.impl;

import com.ms.cardmanagement.domain.CartaoEntity;
import com.ms.cardmanagement.domain.SituacaoCartao;
import com.ms.cardmanagement.domain.TipoCartao;
import com.ms.cardmanagement.dto.CartaoResponse;
import com.ms.cardmanagement.dto.CriarCartaoRequest;
import com.ms.cardmanagement.event.CartaoAtivadoEvent;
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

        CartaoEntity cartaoEntityFisico = new CartaoEntity();
        cartaoEntityFisico.setCpf(request.getCpf());
        cartaoEntityFisico.setNomeImpresso(request.getNomeImpresso());
        cartaoEntityFisico.setProduto(request.getProduto());
        cartaoEntityFisico.setSubproduto(request.getSubproduto());
        cartaoEntityFisico.setTipoCartao(TipoCartao.FISICO);
        cartaoEntityFisico.setSituacao(SituacaoCartao.PENDENTE_ATIVACAO);
        cartaoEntityFisico.setDataCriacao(LocalDateTime.now());

        CartaoEntity cartaoEntityOnline = new CartaoEntity();
        cartaoEntityOnline.setCpf(request.getCpf());
        cartaoEntityOnline.setNomeImpresso(request.getNomeImpresso());
        cartaoEntityOnline.setProduto(request.getProduto());
        cartaoEntityOnline.setSubproduto(request.getSubproduto());
        cartaoEntityOnline.setTipoCartao(TipoCartao.ONLINE);
        cartaoEntityOnline.setSituacao(SituacaoCartao.ATIVO);
        cartaoEntityOnline.setDataCriacao(LocalDateTime.now());

        List<CartaoEntity> cartoesSalvos = cartaoRepository.saveAll(List.of(cartaoEntityFisico, cartaoEntityOnline));
        log.info("Cartões salvos com sucesso no banco. Quantidade: {}", cartoesSalvos.size());

        cartoesSalvos.forEach(cartaoEntity -> {
            CartaoCriadoEvent event = toEvent(cartaoEntity);
            cartaoProducer.publicarCartaoCriado(event);
            log.info("Evento publicado para cartão ID {} - Tipo: {}", cartaoEntity.getId(), cartaoEntity.getTipoCartao());
        });

        return cartoesSalvos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CartaoResponse ativarCartao(Long id) {
        log.info("Iniciando ativação do cartão físico ID: {}", id);
        CartaoEntity cartaoEntity = cartaoRepository.findById(id)
                .orElseThrow(() -> new CartaoException("Cartão não encontrado para o ID: " + id));

        if (cartaoEntity.getTipoCartao() != TipoCartao.FISICO) {
            log.warn("Tentativa de ativar cartão que não é físico. ID: {}, Tipo: {}", id, cartaoEntity.getTipoCartao());
            throw new CartaoException("Só é permitido ativar cartão do tipo FÍSICO.");
        }

        if (cartaoEntity.getSituacao() != SituacaoCartao.PENDENTE_ATIVACAO) {
            log.warn("Tentativa de ativar cartão que não está pendente. ID: {}, Situação: {}", id, cartaoEntity.getSituacao());
            throw new CartaoException("Cartão deve estar com status PENDENTE_ATIVACAO para ativação.");
        }

        cartaoEntity.setSituacao(SituacaoCartao.ATIVO);
        cartaoEntity.setDataAtualizacao(LocalDateTime.now()); // precisa ter esse campo na entidade
        CartaoEntity cartaoEntityAtivado = cartaoRepository.save(cartaoEntity);
        log.info("Cartão físico ID: {} ativado com sucesso.", id);

        CartaoAtivadoEvent event = toAtivadoEvent(cartaoEntityAtivado);
        cartaoProducer.publicarCartaoAtivado(event);
        log.info("Evento CartaoAtivadoEvent publicado para cartão ID: {}", id);

        return toResponse(cartaoEntityAtivado);
    }

    private CartaoResponse toResponse(CartaoEntity cartaoEntity) {
        return CartaoResponse.builder()
                .id(cartaoEntity.getId())
                .cpf(cartaoEntity.getCpf())
                .nomeImpresso(cartaoEntity.getNomeImpresso())
                .produto(cartaoEntity.getProduto())
                .subproduto(cartaoEntity.getSubproduto())
                .tipoCartao(cartaoEntity.getTipoCartao())
                .situacao(cartaoEntity.getSituacao())
                .dataCriacao(cartaoEntity.getDataCriacao())
                .build();
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
