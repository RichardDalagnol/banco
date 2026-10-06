package com.example.banco.exceptions;

import com.example.banco.models.Idempotency;

import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.*;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalErrors {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalErrors.class);

    private ResponseEntity<ProblemDetail> error(HttpStatus status, String code, String message) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, message);
        p.setTitle(code);
        p.setProperty("code", code);
        return ResponseEntity.status(status).body(p);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> business(BusinessException e) {
        return error(e.status, e.code, e.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class, 
            ConstraintViolationException.class,
            MissingRequestHeaderException.class, 
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, 
            HandlerMethodValidationException.class,
            DataIntegrityViolationException.class})
    public ResponseEntity<ProblemDetail> invalid(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Verifique os campos, parâmetros e headers da requisição");
    }

}
