package com.nit.placement_portal.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFoundReturns404WithMessage() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/companies/x");

        ResponseEntity<Map<String, Object>> response =
                handler.handleNotFound(new ResourceNotFoundException("Company Not Found"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Company Not Found", response.getBody().get("message"));
        assertEquals("/api/companies/x", response.getBody().get("path"));
    }

    @Test
    void unauthorizedReturns401() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");

        ResponseEntity<Map<String, Object>> response =
                handler.handleUnauthorized(new UnauthorizedException("Invalid username or password"), request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().get("message"));
    }

    @Test
    void badRequestReturns400() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/complete-registration");

        ResponseEntity<Map<String, Object>> response =
                handler.handleBadRequest(new BadRequestException("Token already used"), request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token already used", response.getBody().get("message"));
    }

    @Test
    void runtimeExceptionIsMaskedAs500() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/public/students");

        ResponseEntity<Map<String, Object>> response =
                handler.handleRuntime(new RuntimeException("raw db failure"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong. Please try again later.", response.getBody().get("message"));
    }
}
