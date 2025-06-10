package com.ms.cardmanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    CARTAO_NAO_ENCONTRADO("CARTAO-404", "Cartão não encontrado."),
    ATIVACAO_INVALIDA("CARTAO-400", "Cartão não está elegível para ativação."),
    CANCELAMENTO_INVALIDO("CARTAO-400", "Cartão não está elegível para cancelamento."),
    ERRO_DESCONHECIDO("GENERIC-500", "Erro interno inesperado.");

    private final String code;
    private final String defaultMessage;
}
