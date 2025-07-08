package com.example.apptive_3team.controller;

import com.example.apptive_3team.ApiResponse;
import com.example.apptive_3team.dto.UserRequestDTO;
import com.example.apptive_3team.dto.UserResponseDTO;
import com.example.apptive_3team.entity.User;
import com.example.apptive_3team.service.KakaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @PostMapping("/kakao/signup")
    public ResponseEntity<?> kakaoLogin(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO responseDTO = kakaoService.signUp(requestDTO);
        return ResponseEntity.ok(ApiResponse.success("요청이 정상적으로 수행되었습니다.", responseDTO));
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> getMyInfo(@RequestHeader("Authorization") String JwtToken) {
        String jwtToken = JwtToken.replace("Bearer ", "");
        Long userId = kakaoService.getUserIdFromJwtToken(jwtToken);

        User user = kakaoService.findUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회 성공", user));
    }

}