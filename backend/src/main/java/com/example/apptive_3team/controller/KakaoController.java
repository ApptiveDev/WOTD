package com.example.apptive_3team.controller;

import com.example.apptive_3team.ApiResponse;
import com.example.apptive_3team.dto.UserRequestDTO;
import com.example.apptive_3team.dto.UserResponseDTO;
import com.example.apptive_3team.service.KakaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

/**
 * 카카오 로그인 또는 회원가입 처리
 * 요청 DTO로 accessToken과 agree를 받고, 사용자 정보와 JWT 포함 응답 반환
 */
@PostMapping("/login")
public ResponseEntity<?> kakaoLogin(@Valid @RequestBody UserRequestDTO requestDTO) {
    UserResponseDTO responseDTO = kakaoService.loginOrRegisterWithToken(requestDTO);
    return ResponseEntity.ok(ApiResponse.success("요청이 정상적으로 수행되었습니다.", responseDTO));
    }
}
