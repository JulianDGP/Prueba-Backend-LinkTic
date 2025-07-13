package com.ms.inventory.infrastructure.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@ControllerAdvice(basePackageClasses = InventoryController.class)
public class ApiExceptionHandler {


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDocument> handleResponseStatus(ResponseStatusException ex) {
        ErrorObject err = ErrorObject.builder()
                .status(String.valueOf(ex.getStatusCode().value()))
                .title(ex.getStatusCode().toString())
                .detail(ex.getReason())
                .build();

        ErrorDocument doc = new ErrorDocument(List.of(err));
        return ResponseEntity
                .status(ex.getStatusCode())
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
