package com.ms.cardmanagement.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cartao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cpf;

    private String nomeImpresso;

    private String produto;

    private String subproduto;

    @Enumerated(EnumType.STRING)
    private TipoCartao tipoCartao;

    @Enumerated(EnumType.STRING)
    private SituacaoCartao situacao;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    private LocalDateTime dataAtivacao;
}
