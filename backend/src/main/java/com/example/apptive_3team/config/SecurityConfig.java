package com.example.apptive_3team.config;

import com.example.apptive_3team.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // ✅ 여기는 로그인 안 해도 되는 공개 경로 (일단 모두 허용)
                        .requestMatchers("/**").permitAll()
                        //  "/auth/**", "/users/**", "/index.html"

                        // 🔒 나머지는 로그인(인증) 필요
                        .anyRequest().authenticated()
                )
                // JwtAuthenticationFilter 등록 (jwtUtil 주입해서 생성)
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}