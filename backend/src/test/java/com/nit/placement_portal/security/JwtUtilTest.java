package com.nit.placement_portal.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;

class JwtUtilTest {

    private static final String SECRET = "test-secret-key-that-is-at-least-32-characters-long-1234567890";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, 3600000L);
    }

    @Test
    void generatesTokenAndExtractsSubjectAndRole() {
        String token = jwtUtil.generateToken("2024CS001", "STUDENT");

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3, "JWT must have header.payload.signature");

        Claims claims = jwtUtil.extractClaims(token);
        assertEquals("2024CS001", claims.getSubject());
        assertEquals("STUDENT", claims.get("role", String.class));
    }

    @Test
    void rejectsTokenSignedWithDifferentSecret() {
        JwtUtil otherIssuer = new JwtUtil("a-completely-different-secret-key-1234567890-abcdef", 3600000L);
        String foreignToken = otherIssuer.generateToken("admin", "ADMIN");

        assertThrows(Exception.class, () -> jwtUtil.extractClaims(foreignToken));
    }

    @Test
    void rejectsExpiredToken() throws InterruptedException {
        JwtUtil shortLived = new JwtUtil(SECRET, 1L);
        String token = shortLived.generateToken("user", "STUDENT");

        Thread.sleep(5);

        assertThrows(Exception.class, () -> shortLived.extractClaims(token));
    }
}
