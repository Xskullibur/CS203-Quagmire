package com.project.G1_T3.authentication.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "jwtService", jwtService);
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "handlerExceptionResolver", handlerExceptionResolver);
        ReflectionTestUtils.setField(jwtAuthenticationFilter, "applicationContext", applicationContext);

        when(applicationContext.getBean(CustomUserDetailsService.class)).thenReturn(userDetailsService);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_NoAuthHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_InvalidAuthHeaderFormat() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Invalid Format");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String username = "user@example.com";
        UserDetails userDetails = mock(UserDetails.class);
        when(jwtService.extractUsername("valid_token")).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        doNothing().when(jwtService).validateToken("valid_token", userDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid_token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("invalid_token"))
                .thenThrow(new InvalidTokenException("Invalid token", "invalid_token"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(eq(request), eq(response), isNull(),
                any(InvalidTokenException.class));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_ExpiredToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer expired_token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String username = "user@example.com";
        UserDetails userDetails = mock(UserDetails.class);
        when(jwtService.extractUsername("expired_token")).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        doThrow(new InvalidTokenException("Token has expired", "expired_token"))
                .when(jwtService).validateToken("expired_token", userDetails);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(eq(request), eq(response), isNull(),
                any(InvalidTokenException.class));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_UserNotFound() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String username = "nonexistent@example.com";
        when(jwtService.extractUsername("valid_token")).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenThrow(new RuntimeException("User not found"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(handlerExceptionResolver).resolveException(eq(request), eq(response), isNull(),
                any(RuntimeException.class));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}