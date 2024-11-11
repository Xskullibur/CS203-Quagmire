package com.project.G1_T3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(frontendUrl, "http://www.quagmire.site", "http://quagmire.site",
                        "http://localhost:3000", "https://quagmire-frontend-alb-1718208115.us-east-1.elb.amazonaws.com",
                        "https://quagmire.site", "https://www.quagmire.site", "https://api.quagmire.site",
                        "http://api.quagmire.site")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(frontendUrl);
        config.addAllowedOrigin("http://www.quagmire.site");
        config.addAllowedOrigin("http://quagmire.site");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("https://quagmire-frontend-alb-1718208115.us-east-1.elb.amazonaws.com");
        config.addAllowedOrigin("https://quagmire.site");
        config.addAllowedOrigin("https://www.quagmire.site");
        config.addAllowedOrigin("https://api.quagmire.site");
        config.addAllowedOrigin("http://api.quagmire.site");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}
