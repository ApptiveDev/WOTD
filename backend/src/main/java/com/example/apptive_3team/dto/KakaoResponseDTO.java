package com.example.apptive_3team.dto;
import java.util.Map;

public record KakaoResponseDTO(
        Long id,
        String connected_at,
        Map<String, Object> properties,
        KakaoAccount kakao_account
) {
    public record KakaoAccount(
            Boolean profile_nickname_needs_agreement,
            Profile profile
    ) {}

    public record Profile(
            String nickname,
            Boolean is_default_nickname
    ) {}
}

