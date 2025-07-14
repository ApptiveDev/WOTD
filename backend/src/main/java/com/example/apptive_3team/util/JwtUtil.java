package com.example.apptive_3team.util;

import com.example.apptive_3team.exception.ErrorCode;
import com.example.apptive_3team.exception.KakaoLogin.JwtValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import io.jsonwebtoken.security.SignatureException;
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

    // JWT 유효성 검사 후 파싱하여 user_id 추출 과정
    public String getUserIdFromToken(String token) {

        // ✅ Bearer 접두사 제거
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("userId", String.class);
        }
        // ✅ 유효성 검사
        catch (ExpiredJwtException e) {
            throw new JwtValidationException(ErrorCode.JWT_EXPIRED_ERROR, e);
        } catch (MalformedJwtException e) {
            throw new JwtValidationException(ErrorCode.JWT_MALFORMED_ERROR, e);
        } catch (SignatureException e) {
            throw new JwtValidationException(ErrorCode.JWT_SIGNATURE_INVALID_ERROR, e);
        } catch (Exception e) {
            throw new JwtValidationException(ErrorCode.JWT_VALIDATION_ERROR, e);
        }
    }
}
