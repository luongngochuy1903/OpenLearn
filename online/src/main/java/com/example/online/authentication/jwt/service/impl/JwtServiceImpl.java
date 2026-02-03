package com.example.online.authentication.jwt.service.impl;

import com.example.online.authentication.authenticate.controller.AuthenticationController;
import com.example.online.domain.model.User;
import com.example.online.exception.ForbiddenException;
import com.example.online.authentication.jwt.service.JwtService;
import com.example.online.exception.UnauthorizedException;
import com.example.online.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${app.jwt.secret}")
    private String SECRET_KEY; // >= 32 ký tự
    private static final Logger LOG = LoggerFactory.getLogger(JwtServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public String generateToken(User userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    //Tạo token với extra Claims
    public String generateToken(Map<String, Object> extraClaims, User userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setId(UUID.randomUUID().toString())
                .setSubject(userDetails.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, User userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getId().toString()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration); //input là Claims, output là type của getExpiration
    }

    public String extractJti(String token){
        return extractClaim(token, Claims::getId); //input là Claims, output là type của getExpiration
    }

    //Móc ra các Claims
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            LOG.warn("Token expired!");
            throw new UnauthorizedException("Token đã hết hạn");
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            LOG.warn("Token invalid!");
            throw new UnauthorizedException("Token không hợp lệ");
        } catch (JwtException e) {
            LOG.warn("Cannot validate Token somehow!");
            throw new UnauthorizedException("Không thể xác thực token");
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){ // Hàm Generic type <T> có kiểu trả T được lấy từ lambda Function<Claims, T> có input là CLaims và output là T
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
}
