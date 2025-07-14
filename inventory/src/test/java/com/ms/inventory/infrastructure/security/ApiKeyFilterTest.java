package com.ms.inventory.infrastructure.security;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.FilterChain;

import static org.mockito.Mockito.*;
class ApiKeyFilterTest {
    private ApiKeyFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ApiKeyFilter();
        // inyectamos la clave que el filtro espera
        ReflectionTestUtils.setField(filter, "expectedKey", "secret-key");
    }

    @Test
    void shouldNotFilter_pathsToSkip() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/swagger-ui/index.html");
        assertTrue(filter.shouldNotFilter(req));

        req.setRequestURI("/v3/api-docs");
        assertTrue(filter.shouldNotFilter(req));

        req.setRequestURI("/actuator/health");
        assertTrue(filter.shouldNotFilter(req));
    }

    @Test
    void shouldFilter_otherPaths() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/inventory/items");
        assertFalse(filter.shouldNotFilter(req));
    }

    @Test
    void doFilterInternal_missingOrInvalidKey_returns401JsonApi() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/inventory/items");
        // no header present
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(req, res, chain);

        assertEquals(401, res.getStatus());
        assertEquals("application/vnd.api+json", res.getContentType());

        String json = res.getContentAsString();
        assertTrue(json.contains("\"status\":\"401\""));
        assertTrue(json.contains("\"title\":\"Unauthorized\""));
        assertTrue(json.contains("\"detail\":\"Invalid or missing API key\""));

        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_invalidKey_delegatesToUnauthorized() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/inventory/items");
        // clave **incorrecta** (no es null, pero no coincide con expectedKey)
        req.addHeader("X-API-KEY", "wrong-key");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(req, res, chain);

        assertEquals(401, res.getStatus());
        assertEquals("application/vnd.api+json", res.getContentType());
        String json = res.getContentAsString();
        assertTrue(json.contains("\"status\":\"401\""));
        assertTrue(json.contains("\"detail\":\"Invalid or missing API key\""));

        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_validKey_delegatesToChain() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/inventory/items");
        req.addHeader("X-API-KEY", "secret-key");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(req, res, chain);

        // no ha cambiado el status ni el contentType
        assertEquals(200, res.getStatus());
        assertNull(res.getContentType());

        verify(chain).doFilter(req, res);
    }

    @Test
    void doFilter_skippedPaths_delegatesDirectly() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/swagger-ui/index.html");
        // incluso sin clave
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        // no debe escribir nada en la respuesta
        assertEquals("", res.getContentAsString());
    }
}