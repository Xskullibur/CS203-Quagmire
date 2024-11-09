package com.project.G1_T3.authentication.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ValidTokenProvided_AuthenticationSetInSecurityContext() throws ServletException, IOException {

        String token = "Bearer valid_token";
        String username = "testuser";
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtService.extractUsername("valid_token")).thenReturn(username);
        when(applicationContext.getBean(CustomUserDetailsService.class)).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(anyString(), any())).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_InvalidTokenProvided_NoAuthenticationSetInSecurityContext()
            throws ServletException, IOException {

        String token = "Bearer invalid_token";
        String username = "testuser";
        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtService.extractUsername("invalid_token")).thenReturn(username);
        when(applicationContext.getBean(CustomUserDetailsService.class)).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(username, userDetails)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_NoAuthorizationHeaderProvided_ThrowsInvalidTokenException() {

        when(request.getHeader("Authorization")).thenReturn(null);
        doThrow(new InvalidTokenException("Invalid token format", null)).when(jwtService).validateTokenFormat(null);

        assertThrows(InvalidTokenException.class, () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
        verify(jwtService).validateTokenFormat(null);
    }

    @Test
    void doFilterInternal_InvalidTokenFormatProvided_ThrowsInvalidTokenException() {

        String token = "InvalidFormat token";
        when(request.getHeader("Authorization")).thenReturn(token);
        doThrow(new InvalidTokenException("Invalid token format", token)).when(jwtService).validateTokenFormat(token);

        assertThrows(InvalidTokenException.class,
                () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
    }

    @Test
    void shouldNotFilter_NonAuthenticationPathProvided_ReturnsFalse() throws ServletException {

        when(request.getRequestURI()).thenReturn("/api/users");

        boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

        assertFalse(result);
    }
}
