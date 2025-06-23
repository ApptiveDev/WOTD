package com.example.apptive_3team.dto;

// 카카오 API 응답을 받아오는 외부 API 호출용 DTO

import java.util.Map;

public record KakaoResponseDTO(
        Long id,
        Map<String, Object> properties
) {}