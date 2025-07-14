package com.example.apptive_3team.config;

import com.example.apptive_3team.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // "Bearer " 제거

            try {
                if (isKakaoToken(token)) {
                    String kakaoUserId = validateKakaoToken(token);
                    if (kakaoUserId != null) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        kakaoUserId, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } else {
                    log.info("토큰 유효성 검사 및 userId 추출");

                    String userId = jwtUtil.getUserIdFromToken(token);

                    // 사용자 인증 객체
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                log.error("토큰 검증 중 예외 발생", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isKakaoToken(String token) {
        // Kakao token인지 단순히 판단하는 로직 (필요시 개선 가능)
        return token.length() > 30 && !token.contains(".");
    }

    private String validateKakaoToken(String kakaoAccessToken) {
        try {
            WebClient webClient = WebClient.create("https://kapi.kakao.com");
            Map<String, Object> result = webClient.get()
                    .uri("/v1/user/access_token_info")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (result != null && result.containsKey("id")) {
                return String.valueOf(result.get("id")); // 카카오 유저 ID
            } else {
                log.warn("Kakao access_token 검증 실패: 응답 없음 또는 id 없음");
                return null;
            }
        } catch (Exception e) {
            log.error("Kakao access_token 검증 중 예외 발생", e);
            return null;
        }
    }
}
