package com.ms.cardmanagement.service.impl;

import com.ms.cardmanagement.domain.Cartao;
import com.ms.cardmanagement.domain.SituacaoCartao;
import com.ms.cardmanagement.domain.TipoCartao;
import com.ms.cardmanagement.dto.CartaoResponse;
import com.ms.cardmanagement.dto.CriarCartaoRequest;
import com.ms.cardmanagement.event.CartaoCriadoEvent;
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

        Cartao cartaoFisico = new Cartao();
        cartaoFisico.setCpf(request.getCpf());
        cartaoFisico.setNomeImpresso(request.getNomeImpresso());
        cartaoFisico.setProduto(request.getProduto());
        cartaoFisico.setSubproduto(request.getSubproduto());
        cartaoFisico.setTipoCartao(TipoCartao.FISICO);
        cartaoFisico.setSituacao(SituacaoCartao.PENDENTE_ATIVACAO);
        cartaoFisico.setDataCriacao(LocalDateTime.now());

        Cartao cartaoOnline = new Cartao();
        cartaoOnline.setCpf(request.getCpf());
        cartaoOnline.setNomeImpresso(request.getNomeImpresso());
        cartaoOnline.setProduto(request.getProduto());
        cartaoOnline.setSubproduto(request.getSubproduto());
        cartaoOnline.setTipoCartao(TipoCartao.ONLINE);
        cartaoOnline.setSituacao(SituacaoCartao.ATIVO);
        cartaoOnline.setDataCriacao(LocalDateTime.now());

        List<Cartao> cartoesSalvos = cartaoRepository.saveAll(List.of(cartaoFisico, cartaoOnline));
        log.info("Cartões salvos com sucesso no banco. Quantidade: {}", cartoesSalvos.size());

        cartoesSalvos.forEach(cartao -> {
            CartaoCriadoEvent event = toEvent(cartao);
            cartaoProducer.publicar(event);
            log.info("Evento publicado para cartão ID {} - Tipo: {}", cartao.getId(), cartao.getTipoCartao());
        });

        return cartoesSalvos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CartaoResponse toResponse(Cartao cartao) {
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

    private CartaoCriadoEvent toEvent(Cartao cartao) {
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
}
