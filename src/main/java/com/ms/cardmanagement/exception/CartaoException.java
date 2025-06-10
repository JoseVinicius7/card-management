package com.ms.cardmanagement.exception;

import lombok.Getter;

@Getter
public class CartaoException extends RuntimeException {

    private final ErrorCode errorCode;

    public CartaoException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public CartaoException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
