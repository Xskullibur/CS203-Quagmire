package com.project.G1_T3.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.project.G1_T3.user.model.User;
import com.project.G1_T3.user.service.UserService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKeyString;
    @Value("${jwt.expiration-time}")
    private long expirationTime;

    private Key secretKey;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {

        if (secretKeyString == null || secretKeyString.isEmpty()) {
            throw new IllegalStateException("JWT secret key is not set or is empty in application.yml");
        }

        if (expirationTime <= 0) {
            throw new IllegalStateException("JWT expiration time is not set or is invalid in application.yml");
        }

        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, User userDetails) {
        return buildToken(extraClaims, userDetails, expirationTime);
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    private String buildToken(Map<String, Object> extraClaims, User userDetails, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .setHeaderParam("typ", "JWT")
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<User> validateTokenAndGetUser(String token) {

        try {

            String jwtToken = removeTokenPrefix(token);
            String jwtUsername = extractUsername(jwtToken);

            if (jwtUsername == null || jwtUsername.isEmpty()) {
                return Optional.empty();
            }

            Optional<User> userOptional = userService.findByUsername(jwtUsername);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (isTokenValid(jwtToken, user)) {
                    return userOptional;
                }
            }

            return Optional.empty();

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isTokenValid(String token, UserDetails user) {
        return !isTokenExpired(token) && extractUsername(token).equals(user.getUsername());
    }

    private String removeTokenPrefix(String token) {
        return token.substring(7);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}