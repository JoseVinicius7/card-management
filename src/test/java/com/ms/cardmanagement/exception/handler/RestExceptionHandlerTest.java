package com.ms.cardmanagement.exception.handler;

import com.ms.cardmanagement.exception.CartaoException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void deveRetornarBadRequestComMensagemDaException() {
        String mensagem = "Erro no cartão";
        CartaoException ex = new CartaoException(mensagem);

        ResponseEntity<String> response = handler.handleCartaoException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(mensagem, response.getBody());
    }
}
