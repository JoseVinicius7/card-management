package com.ms.cardmanagement.event;

import java.time.LocalDateTime;

public record CartaoCriadoEvent(
        Long id,
        String cpf,
        String nomeImpresso,
        String produto,
        String subproduto,
        String tipoCartao,
        String situacao,
        LocalDateTime dataCriacao
) {
}
