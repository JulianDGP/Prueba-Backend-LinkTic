package com.ms.inventory.infrastructure.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.inventory.infrastructure.adapters.in.web.ApiExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    @Value("${inventory.api.key}")
    private String expectedKey;

    private final ObjectMapper mapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        // no proteger la UI de swagger ni los docs OpenAPI
        String path = req.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String apiKey = req.getHeader("X-API-KEY");
        if (apiKey == null || !apiKey.equals(expectedKey)) {
            // Construyo el error JSON:API manualmente
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/vnd.api+json");
            ApiExceptionHandler.ErrorObject err = ApiExceptionHandler.ErrorObject.builder()
                    .status("401")
                    .title("Unauthorized")
                    .detail("Invalid or missing API key")
                    .build();
            ApiExceptionHandler.ErrorDocument doc = new ApiExceptionHandler.ErrorDocument(List.of(err));
            mapper.writeValue(res.getWriter(), doc);
            return; // no continues al chain
        }
        chain.doFilter(req, res);
    }
}
