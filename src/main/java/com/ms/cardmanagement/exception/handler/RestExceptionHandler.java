package com.ms.cardmanagement.exception.handler;

import com.ms.cardmanagement.exception.CartaoException;
import com.ms.cardmanagement.exception.ErrorCode;
import com.ms.cardmanagement.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(CartaoException.class)
    public ResponseEntity<ErrorResponse> handleCartaoException(CartaoException ex) {
        ErrorCode code = ex.getErrorCode();
        ErrorResponse response = new ErrorResponse(
                code.getCode(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro inesperado no sistema", ex);
        ErrorResponse response = new ErrorResponse(
                ErrorCode.ERRO_DESCONHECIDO.getCode(),
                ErrorCode.ERRO_DESCONHECIDO.getDefaultMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
