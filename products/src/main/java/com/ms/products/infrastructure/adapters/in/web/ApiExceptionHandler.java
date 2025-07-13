package com.ms.products.infrastructure.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice(basePackages = "com.ms.products.infrastructure")
public class ApiExceptionHandler {

    // 1) ResponseStatusException → delegamos su código y mensaje
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDocument> handleResponseStatus(ResponseStatusException ex) {
        return buildError(
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason()
        );
    }

    // 2) Validación de @Valid en DTOs → 400 Bad Request con detalle de campo
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDocument> handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Invalid request");
        return buildError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                detail
        );
    }

    // 3) IllegalArgumentException y NoSuchElementException → 400 Bad Request
    @ExceptionHandler({ IllegalArgumentException.class, NoSuchElementException.class })
    public ResponseEntity<ErrorDocument> handleBadRequest(RuntimeException ex) {
        return buildError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
    }


    // 4) Cualquier otro error → 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDocument> handleAllUnexpected(Exception ex) {
        log.error("Error inesperado en Productos", ex);
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ha ocurrido un error interno. Por favor inténtalo más tarde."
        );
    }

    private ResponseEntity<ErrorDocument> buildError(int status, String title, String detail) {
        ErrorObject err = ErrorObject.builder()
                .status(String.valueOf(status))
                .title(title)
                .detail(detail)
                .build();
        ErrorDocument doc = new ErrorDocument(List.of(err));
        return ResponseEntity
                .status(status)
                .contentType(MediaType.valueOf("application/vnd.api+json"))
                .body(doc);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorDocument {
        private List<ErrorObject> errors;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorObject {
        private String id;
        private String status;
        private String code;
        private String title;
        private String detail;
    }
}
