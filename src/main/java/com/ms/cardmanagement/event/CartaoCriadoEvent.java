package com.ms.cardmanagement.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartaoCriadoEvent {
    private Long id;
    private String cpf;
    private String nomeImpresso;
    private String produto;
    private String subproduto;
    private String tipoCartao;
    private String situacao;
    private LocalDateTime dataCriacao;
}
