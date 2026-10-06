package com.example.banco.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    public final HttpStatus status;
    public final String code;

    public BusinessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public static BusinessException missing() {
        return new BusinessException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Recurso não encontrado");
    }

    public static BusinessException conflict(String code, String message) {
        return new BusinessException(HttpStatus.CONFLICT, code, message);
    }
}
