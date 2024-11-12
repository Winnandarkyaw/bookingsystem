package com.example.bookingsys.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Method to generate the JWT token
    public String generateToken(String username) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());  // Use key from secret

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // Token expiration in 10 hours
                .signWith(key, SignatureAlgorithm.HS256)  // Sign token with the HMAC-SHA256 algorithm
                .compact();
    }

    // Method to extract username from the JWT token
    public String extractUsername(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());  // Use the same secret to parse the token

        return Jwts.parserBuilder()
                .setSigningKey(key)  // Set the signing key for verification
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Method to validate the token
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Method to check if the token is expired
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))  // Use the signing key for verification
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}

