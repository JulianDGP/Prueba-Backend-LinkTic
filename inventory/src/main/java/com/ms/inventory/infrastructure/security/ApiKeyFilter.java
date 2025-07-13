package com.ms.inventory.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    @Value("${inventory.api.key}")
    private String expectedKey;

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
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String key = req.getHeader("X-API-KEY");
        if (key == null || !key.equals(expectedKey)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid or missing API key");
        }
        chain.doFilter(req, res);
    }
}
