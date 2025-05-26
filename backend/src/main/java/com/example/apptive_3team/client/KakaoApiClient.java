package com.example.apptive_3team.client;

import com.example.apptive_3team.dto.KakaoUserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class KakaoApiClient {
    private final WebClient webClient = WebClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoUserResponse getUserInfo(String accessToken) {
        try {
            return webClient.get()
                    .uri("https://kapi.kakao.com/v2/user/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .exchangeToMono(response -> {
                        int statusCode = response.statusCode().value();
                        return response.bodyToMono(String.class).flatMap(body -> {
                            System.out.println("📥 상태코드: " + statusCode);
                            System.out.println("📥 바디: " + body);

                            if (statusCode == 401 || body.contains("\"code\": -401")) {
                                return Mono.error(new RuntimeException("유효하지 않은 Kakao access token입니다."));
                            }

                            try {
                                KakaoUserResponse userResponse = objectMapper.readValue(body, KakaoUserResponse.class);
                                return Mono.just(userResponse);
                            } catch (Exception e) {
                                return Mono.error(new RuntimeException("Kakao 응답 파싱 실패", e));
                            }
                        });
                    })
                    .block();

        } catch (Exception e) {
            System.out.println("❗ 최종 예외: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            throw new RuntimeException("카카오 사용자 정보 요청 실패", e);
        }
    }
}