package ru.javabegin.backend.found404.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.expirationMs}")
    private long validityInMilliseconds;

    private Key secretKey;

    @PostConstruct
    protected void init() {
        // Если секретный ключ слишком короткий, его необходимо расширить
        if (secretKeyString.length() < 64) {  // 64 символа = 512 бит (примерно)
            throw new IllegalArgumentException("Секретный ключ должен быть как минимум 512 бит (64 символа).");
        }
        // Используем метод для создания ключа
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // Генерация токена
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // Получение email из токена
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Проверка валидности токена
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
