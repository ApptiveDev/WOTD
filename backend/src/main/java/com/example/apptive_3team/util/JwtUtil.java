package com.example.apptive_3team.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expireTimeMs = 1000L * 60 * 60 * 24 * 365 * 50; // 50년

    // 생성자에서 Base64 디코딩하여 Key 생성
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    // 토큰 생성
    public String createToken(String providerId) {
        return Jwts.builder()
                .setSubject("kakao-login")
                .claim("userId", providerId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(key)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            System.out.println("JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }

    // 토큰에서 userId 추출
    public String getUserIdFromToken(String token) {

        // ✅ Bearer 접두사 제거
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);
    }


}
