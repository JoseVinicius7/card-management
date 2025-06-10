package com.ms.cardmanagement.dto;

import com.ms.cardmanagement.domain.SituacaoCartao;
import com.ms.cardmanagement.domain.TipoCartao;

import java.time.LocalDateTime;

public record CartaoResponse(
        Long id,
        String cpf,
        String nomeImpresso,
        String produto,
        String subproduto,
        TipoCartao tipoCartao,
        SituacaoCartao situacao,
        LocalDateTime dataCriacao
) {}
