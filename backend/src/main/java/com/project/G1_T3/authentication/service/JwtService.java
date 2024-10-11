package com.project.G1_T3.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.project.G1_T3.user.model.User;
import com.project.G1_T3.common.exception.InvalidTokenException;
import com.project.G1_T3.user.model.CustomUserDetails;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    @Value("${jwt.expiration-time.authentication}")
    private long expirationTime;

    @Value("${jwt.expiration-time.email-verification}")
    private long emailVerificationExpirationTime;

    private Key secretKey;

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

    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, User userDetails) {
        return buildToken(extraClaims, userDetails, expirationTime);
    }

    public String generateEmailVerificationToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("purpose", "email_verification");
        return buildToken(claims, user, emailVerificationExpirationTime);
    }

    public String validateEmailVerificationToken(String token) {
        try {
            Claims claims = extractAllClaims(token);

            if (!"email_verification".equals(claims.get("purpose"))) {
                throw new InvalidTokenException("Invalid token purpose", token);
            }

            if (claims.getExpiration().before(new Date())) {
                throw new InvalidTokenException("Token has expired", token);
            }

            return claims.getSubject();

        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token has expired", token);
        } catch (MalformedJwtException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid token", token);
        }
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

    public void validateToken(String token, CustomUserDetails userDetails) {
        try {

            final String username = extractUsername(token);

            if (!username.equals(userDetails.getUsername())) {
                throw new InvalidTokenException("Invalid token: username mismatch", token);
            }

        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException("Token has expired", token);
        } catch (MalformedJwtException e) {
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid token", token);
        }
    }

    public void validateTokenFormat(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid token format", token);
        }
    }

    public String getEmailFromToken(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String removeTokenPrefix(String token) {
        return token.substring(7);
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

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails user) {
        return !isTokenExpired(token) && extractUsername(token).equals(user.getUsername());
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}