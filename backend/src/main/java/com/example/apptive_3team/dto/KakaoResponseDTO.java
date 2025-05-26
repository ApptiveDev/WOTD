package com.example.apptive_3team.dto;

// 카카오 서버로부터 access token으로 사용자 정보를 받아올 때 그 결과를 담아두는 용도의 DTO

import java.util.Map;

public record KakaoResponseDTO(
        Long id,
        Map<String, Object> properties
) {}