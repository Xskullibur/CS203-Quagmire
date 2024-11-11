package com.project.G1_T3.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.client.RestTemplate;
import com.project.G1_T3.authentication.filter.JwtAuthenticationFilter;
import com.project.G1_T3.user.service.CustomUserDetailsService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @PostConstruct
    public void init() {
        logger.info("SecurityConfig initialized");
    }

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final class SecurityPermissions {

        // Paths that are completely public (no JWT needed)
        private static final List<RequestMapping> PUBLIC_PATHS = Arrays.asList(
                new RequestMapping("/authentication/**"),
                new RequestMapping("/leaderboard/**"),
                new RequestMapping("/profile/**", "GET"),
                new RequestMapping("/tournament/**", "GET"),
                new RequestMapping("/ws/**"),
                new RequestMapping("/matches/**"));

        // Paths that require specific roles
        private static final List<RequestMapping> ADMIN_PATHS = Arrays.asList(
                new RequestMapping("/admin/**"),
                new RequestMapping("/tournament/create"));

        // Paths that require authentication (JWT needed)
        private static final List<RequestMapping> AUTHENTICATED_PATHS = Arrays.asList(
                new RequestMapping("/authentication/update-password"),
                new RequestMapping("/profile/edit"),
                new RequestMapping("/users/**"),
                new RequestMapping("file/**"));
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        filter.setSkipPaths(SecurityPermissions.PUBLIC_PATHS.stream()
                .map(RequestMapping::toRequestMatcher)
                .collect(Collectors.toList()));
        return filter;
    }

    // Helper class to store path and method information
    private static class RequestMapping {
        private final String path;
        private final String method;

        public RequestMapping(String path) {
            this.path = path;
            this.method = null;
        }

        public RequestMapping(String path, String method) {
            this.path = path;
            this.method = method;
        }

        public RequestMatcher toRequestMatcher() {
            return method != null
                    ? new AntPathRequestMatcher(path, method)
                    : new AntPathRequestMatcher(path);
        }
    }

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(customUserDetailsService);
        return provider;
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied: You are not authorized to access this resource");
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain");

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // Configure public paths
                    SecurityPermissions.PUBLIC_PATHS
                            .forEach(mapping -> auth.requestMatchers(mapping.toRequestMatcher()).permitAll());

                    // Configure admin paths
                    SecurityPermissions.ADMIN_PATHS
                            .forEach(mapping -> auth.requestMatchers(mapping.toRequestMatcher()).hasRole("ADMIN"));

                    // Configure authenticated paths
                    SecurityPermissions.AUTHENTICATED_PATHS
                            .forEach(mapping -> auth.requestMatchers(mapping.toRequestMatcher()).authenticated());

                    // Any remaining paths require authentication
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler()))
                .logout(logout -> logout
                        .permitAll()
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_NO_CONTENT)))
                .httpBasic(Customizer.withDefaults())
                .addFilterAfter(jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(frontendUrl, "http://www.quagmire.site", "http://quagmire.site", "http://localhost:3000", "https://quagmire-frontend-alb-1718208115.us-east-1.elb.amazonaws.com", "https://quagmire.site", "https://www.quagmire.site", "https://api.quagmire.site", "http://api.quagmire.site"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
