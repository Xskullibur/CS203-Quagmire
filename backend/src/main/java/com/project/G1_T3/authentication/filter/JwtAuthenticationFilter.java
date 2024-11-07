package com.project.G1_T3.authentication.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.project.G1_T3.authentication.service.JwtService;
import com.project.G1_T3.user.model.CustomUserDetails;
import com.project.G1_T3.user.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Retrieve and validate token format
        final String authHeader = request.getHeader("Authorization");

        jwtService.validateTokenFormat(authHeader);

        // Extract information from header
        final String token = authHeader.substring(7);
        final String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Retrieve user details
            CustomUserDetails userDetails = applicationContext.getBean(CustomUserDetailsService.class)
                    .loadUserByUsername(username);

            // Validate Token
            if (jwtService.isTokenValid(token, userDetails)) {

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();
    
        // Paths that should not be filtered
        List<String> excludedPaths = List.of("/authentication", "/ws", "/leaderboard/user");
        Map<String, List<String>> methodSpecificExclusions = Map.of(
            "/profile", List.of("GET")
        );
    
        boolean isExcludedPath = excludedPaths.stream().anyMatch(path::startsWith);
    
        boolean isMethodSpecificExcluded = methodSpecificExclusions.entrySet().stream()
            .anyMatch(entry -> path.startsWith(entry.getKey()) && entry.getValue().contains(method));
    
        return isExcludedPath || isMethodSpecificExcluded;
    }

}
