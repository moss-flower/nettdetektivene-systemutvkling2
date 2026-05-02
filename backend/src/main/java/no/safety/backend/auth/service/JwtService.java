package no.safety.backend.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service class responsible for JWT token generation, validation, and extraction.
 * Handles both access and refresh token operations using JJWT library.
 */
@Service
@Slf4j
public class JwtService {
    @Value("${application.security.jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${application.security.jwt.durationMillis}")
    private long jwtDurationMillis;

    @Value("${application.security.jwt.refreshDurationMillis}")
    private long refreshDurationMillis;

    /**
     * Derives the signing key from the configured secret key.
     * @return the SecretKey used for signing JWT tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    /**
     * Generates a JWT access token for the specified username.
     * Access tokens have a shorter expiration time configured in application properties.
     * @param username the username to include as the token subject
     * @return the generated JWT access token string
     */
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtDurationMillis))
                .claim("tokenType", "access")
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generates a JWT refresh token for the specified username.
     * Refresh tokens have a longer expiration time configured in application properties.
     * @param username the username to include as the token subject
     * @return the generated JWT refresh token string
     */
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshDurationMillis))
                .claim("tokenType", "refresh")
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the username (subject) from a JWT token.
     * @param token the JWT token to extract the username from
     * @return the username stored as the subject in the token
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates a JWT token by verifying its signature and expiration.
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a JWT token is an access token.
     * @param token the JWT token to check
     * @return true if the token has tokenType claim set to "access", false otherwise
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return "access".equals(claims.get("tokenType", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a JWT token is a refresh token.
     * @param token the JWT token to check
     * @return true if the token has tokenType claim set to "refresh", false otherwise
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return "refresh".equals(claims.get("tokenType", String.class));
        } catch (Exception e) {
            return false;
        }
    }
}
