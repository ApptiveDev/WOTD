package com.example.apptive_3team.controller;

import com.example.apptive_3team.ApiResponse;
import com.example.apptive_3team.dto.UserRequestDTO;
import com.example.apptive_3team.dto.UserResponseDTO;
import com.example.apptive_3team.entity.User;
import com.example.apptive_3team.service.KakaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @PostMapping("/kakao/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRequestDTO requestDTO) {
        log.info("📥 [POST] /kakao/signup API 진입");

        try {
            log.info("🛠 kakaoService.signUp() 호출");
            UserResponseDTO responseDTO = kakaoService.signUp(requestDTO);
            log.info("✅ 회원가입 성공 - userId: {}", responseDTO.id());

            return ResponseEntity.ok(ApiResponse.success("요청이 정상적으로 수행되었습니다.", responseDTO));
        } catch (Exception e) {
            log.error("❌ 회원가입 실패 - error: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler에서 처리
        }
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> getMyInfo(@RequestHeader("Authorization") String JwtToken) {
        log.info("📥 [GET] /users/me API 진입");

        try {
            String jwtToken = JwtToken.replace("Bearer ", "");
            Long userId = kakaoService.getUserIdFromJwtToken(jwtToken);
            log.debug("🔑 추출된 userId: {}", userId);

            log.info("🛠 사용자 정보 조회 시작 - userId: {}", userId);
            User user = kakaoService.findUserById(userId);
            log.info("✅ 사용자 정보 조회 성공 - nickname: {}", user.getName());

            return ResponseEntity.ok(ApiResponse.success("내 정보 조회 성공", user));
        } catch (Exception e) {
            log.error("❌ 사용자 정보 조회 실패 - reason: {}", e.getMessage(), e);
            throw e;
        }
    }

}