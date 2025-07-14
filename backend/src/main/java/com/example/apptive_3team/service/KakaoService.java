package com.example.apptive_3team.service;

import com.example.apptive_3team.config.KakaoApiClient;
import com.example.apptive_3team.dto.KakaoResponseDTO;
import com.example.apptive_3team.dto.UserRequestDTO;
import com.example.apptive_3team.dto.UserResponseDTO;
import com.example.apptive_3team.entity.User;
import com.example.apptive_3team.repository.KakaoRepository;
import com.example.apptive_3team.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final KakaoApiClient kakaoApiClient;
    private final KakaoRepository kakaoRepository;
    private final JwtUtil jwtUtil;

    public UserResponseDTO signUp(UserRequestDTO requestDTO) {
        String accessToken = requestDTO.accessToken();
        Boolean agree = requestDTO.agree();

        KakaoResponseDTO kakaoData = kakaoApiClient.getUserInfo(accessToken);
        String providerId = String.valueOf(kakaoData.id());
        String name = Optional.ofNullable(kakaoData.kakao_account())
                .map(KakaoResponseDTO.KakaoAccount::profile)
                .map(KakaoResponseDTO.Profile::nickname)
                .orElse("카카오사용자");

        // ❗ 이미 가입된 사용자인 경우 예외 발생
        if (kakaoRepository.findByProviderId(providerId).isPresent()) {
            throw new RuntimeException("이미 가입된 사용자입니다.");
        }

        // 새 유저 저장
        User newUser = new User();
        newUser.setProviderId(providerId);
        newUser.setProviderType(User.ProviderType.KAKAO);
        newUser.setName(name);
        newUser.setAllow_notification(agree);
        newUser.setCreatedAt(LocalDateTime.now());
        kakaoRepository.save(newUser);

        // JWT 생성
        String token = jwtUtil.createToken(newUser.getProviderId());

        // 응답 DTO 생성
        return new UserResponseDTO(
                newUser.getId(),
                newUser.getName(),
                newUser.getProviderId(),
                newUser.getProviderType().name(),
                token
        );
    }

    // 토큰에서 userId 추출
    public Long getUserIdFromJwtToken(String jwtToken) {
        String providerId = jwtUtil.getUserIdFromToken(jwtToken);
        return kakaoRepository.findByProviderId(providerId)
                .map(User::getId)
                .orElse(null);
    }

    public User findUserById(Long userId) {
        return kakaoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

}