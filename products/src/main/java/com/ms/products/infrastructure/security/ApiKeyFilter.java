package com.ms.products.infrastructure.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${products.api.key}")
    private String expectedApiKey;

    private final ObjectMapper mapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // No proteger docs ni Swagger UI
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        String apiKey = req.getHeader("X-API-KEY");
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            // Construyo el error JSON:API manualmente
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/vnd.api+json");
            ErrorObject err = ErrorObject.builder()
                    .status("401")
                    .title("Unauthorized")
                    .detail("Invalid or missing API key")
                    .build();
            ErrorDocument doc = new ErrorDocument(List.of(err));
            mapper.writeValue(res.getWriter(), doc);
            return; // no continues al chain
        }
        chain.doFilter(req, res);
    }

    @Getter @Setter @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class ErrorDocument {
        private List<ErrorObject> errors;
    }

    @Getter
    @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    static class ErrorObject {
        private String id;
        private String status;
        private String code;
        private String title;
        private String detail;
    }
}
