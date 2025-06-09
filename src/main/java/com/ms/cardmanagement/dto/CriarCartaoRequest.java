package com.ms.cardmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
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
