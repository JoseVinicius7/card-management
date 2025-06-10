package com.ms.cardmanagement.exception.handler;

import com.ms.cardmanagement.exception.CartaoException;
import com.ms.cardmanagement.exception.ErrorCode;
import com.ms.cardmanagement.exception.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void deveRetornarBadRequest_quandoCartaoExceptionForLancada() {
        CartaoException exception = new CartaoException(ErrorCode.ATIVACAO_INVALIDA);

        ResponseEntity<ErrorResponse> response = handler.handleCartaoException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("CARTAO-400");
        assertThat(response.getBody().getMessage()).isEqualTo("Cartão não está elegível para ativação.");
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    void deveRetornarInternalServerError_quandoExceptionGenericaForLancada() {
        Exception exception = new RuntimeException("Erro genérico");
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("GENERIC-500");
        assertThat(response.getBody().getMessage()).isEqualTo("Erro interno inesperado.");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}
