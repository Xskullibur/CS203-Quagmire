package com.project.G1_T3.authentication.controller;

import com.project.G1_T3.player.model.User;
import com.project.G1_T3.player.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.project.G1_T3.authentication.model.LoginRequest;
import com.project.G1_T3.authentication.model.LoginResponse;

import java.util.Optional;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/authentication")
public class LoginController {
    private static final SecretKey SECRET_KEY = generateSecretKey();
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    private static SecretKey generateSecretKey() {
        String secretString = "PLEASECHANGETHISTOANACTUALVALUETHANKS";
        return Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                String token = Jwts.builder()
                        .setSubject(user.getId().toString())
                        .claim("username", user.getUsername())
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                        .signWith(SECRET_KEY)
                        .compact();
    
                LoginResponse response = new LoginResponse(
                    user.getId().toString(),
                    user.getUsername(),
                    token
                );
    
                return ResponseEntity.ok().body(response);
            }
        }
        
        return ResponseEntity.badRequest().body("Invalid username or password");
    }
}