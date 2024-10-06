package com.project.G1_T3.authentication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Base64;
import java.util.Date;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String TEST_SECRET_KEY = Base64.getEncoder()
            .encodeToString("x8Zk9zgJeqrAmu5OX66SjgBaYhYcB0xb".getBytes());
    private static final long EXPIRATION_TIME = 86400000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKeyString", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "expirationTime", EXPIRATION_TIME);
        jwtService.init();
    }

    @Test
    void generateToken_ValidUser_ReturnsToken() {
        User user = new User();
        user.setUsername("testuser");

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        User user = new User();
        user.setUsername("testuser");

        String token = jwtService.generateToken(user);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals("testuser", extractedUsername);
    }

    @Test
    void validateToken_ValidToken_DoesNotThrowException() {
        User user = new User();
        user.setUsername("testuser");

        String token = jwtService.generateToken(user);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        assertDoesNotThrow(() -> jwtService.validateToken(token, userDetails));
    }

    @Test
    void validateToken_ExpiredToken_ThrowsInvalidTokenException() {
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2 * EXPIRATION_TIME))
                .setExpiration(new Date(System.currentTimeMillis() - EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET_KEY)), SignatureAlgorithm.HS256)
                .compact();

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        InvalidTokenException exception = assertThrows(InvalidTokenException.class,
                () -> jwtService.validateToken(token, userDetails));
        assertEquals("Token has expired", exception.getMessage());
    }

    @Test
    void validateToken_InvalidSignature_ThrowsInvalidTokenException() {
        String invalidSecretKey = Base64.getEncoder().encodeToString("aXNr6daXKr7BekMUBEl6P4V4zxipMkVA".getBytes());
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(invalidSecretKey)), SignatureAlgorithm.HS256)
                .compact();

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        InvalidTokenException exception = assertThrows(InvalidTokenException.class,
                () -> jwtService.validateToken(token, userDetails));
        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void validateToken_UsernameMismatch_ThrowsInvalidTokenException() {
        User user = new User();
        user.setUsername("testuser");

        String token = jwtService.generateToken(user);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn("differentuser");

        assertThrows(InvalidTokenException.class, () -> jwtService.validateToken(token, userDetails));
    }
}