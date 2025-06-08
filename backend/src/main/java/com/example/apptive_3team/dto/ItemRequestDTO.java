package com.example.apptive_3team.dto;

/**
 * 챙길물품의 정보와 사용자 ID를 전달받는 DTO
 *
 * @param accessToken 소셜 로그인 사용자 토큰
 * @param itemDTO 챙길 물품 DTO
 */
public record ItemRequestDTO(String accessToken,
                             ItemDTO itemDTO) {

}
