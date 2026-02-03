package com.example.online.authentication.jwt.service;

import com.example.online.domain.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);
    String generateToken(User userDetails);
    String generateToken(Map<String, Object> extraClaims, User userDetails);
    boolean isTokenValid(String token, User userDetails);
    boolean isTokenExpired(String token);
    Date extractExpiration(String token);
    String extractJti(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
