package com.ms.cardmanagement.dto;

import com.ms.cardmanagement.domain.SituacaoCartao;
import com.ms.cardmanagement.domain.TipoCartao;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CartaoResponse {

    private Long id;
    private String cpf;
    private String nomeImpresso;
    private String produto;
    private String subproduto;
    private TipoCartao tipoCartao;
    private SituacaoCartao situacao;
    private LocalDateTime dataCriacao;
}

