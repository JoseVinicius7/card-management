package com.ms.cardmanagement.event;

import java.time.LocalDateTime;

public record CartaoCanceladoEvent(
        Long id,
        String cpf,
        String tipoCartao,
        LocalDateTime dataCancelamento
) {}
