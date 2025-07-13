package com.ms.inventory.infrastructure.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice(basePackages = "com.ms.inventory.infrastructure")
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

    // 2) Mapea IllegalArgumentException y NoSuchElementException a 400 Bad Request
    @ExceptionHandler({ IllegalArgumentException.class, NoSuchElementException.class })
    public ResponseEntity<ErrorDocument> handleBadRequest(RuntimeException ex) {
        return buildError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage()
        );
    }

    // 3) Cualquier otro error sale como 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDocument> handleAllUnexpected(Exception ex) {
        // opcionalmente logueas:
        log.error("Error inesperado en el endpoint de inventory", ex);
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ha ocurrido un error interno. Por favor inténtalo de nuevo más tarde."
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

    // JSON:API Error Document
    @Getter
    @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ErrorDocument {
        private List<ErrorObject> errors;
    }

    // JSON:API Error Object
    @Getter @Setter @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorObject {
        private String id;
        private String status;
        private String code;
        private String title;
        private String detail;
    }
}
