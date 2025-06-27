package com.example.apptive_3team.config;

import com.example.apptive_3team.dto.KakaoResponseDTO;
import com.example.apptive_3team.exception.KakaoLogin.InvalidKakaoAccessTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private final WebClient kakaoWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoResponseDTO getUserInfo(String accessToken) {
        try {
            String responseBody = kakaoWebClient.get()
                    .uri("/v2/user/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(responseBody, KakaoResponseDTO.class);

        } catch (WebClientResponseException.Unauthorized e) {
            throw new InvalidKakaoAccessTokenException();
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패", e);
        }
    }
}
