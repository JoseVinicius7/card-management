package com.ms.cardmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class CriarCartaoRequest {

    @NotBlank
    private String cpf;

    @NotBlank
    private String nomeImpresso;

    @NotBlank
    private String produto;

    @NotBlank
    private String subproduto;
}
