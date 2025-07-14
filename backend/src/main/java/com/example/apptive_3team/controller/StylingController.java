package com.example.apptive_3team.controller;

import com.example.apptive_3team.ApiResponse;
import com.example.apptive_3team.dto.StylingSuggestionRequestDTO;
import com.example.apptive_3team.dto.StylingSuggestionResponseDTO;
import com.example.apptive_3team.entity.MoodReport;
import com.example.apptive_3team.service.KakaoService;
import com.example.apptive_3team.service.MoodReportService;
import com.example.apptive_3team.service.StylingService;
import com.example.apptive_3team.service.WeatherApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stylingSuggestion")
@RequiredArgsConstructor
public class StylingController {

    private final StylingService stylingService;
    private final MoodReportService moodReportService;
    private final WeatherApiService weatherApiService;
    private final KakaoService kakaoService;

    @PostMapping("/request")
    public ResponseEntity<?> getStylingSuggestionFromMoodReports(@RequestHeader("Authorization") String token,
                                                                 @Valid @RequestBody StylingSuggestionRequestDTO request) {
        log.info("📥 [POST] /styling/request API 호출됨");

        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = kakaoService.getUserIdFromJwtToken(jwtToken);
            log.debug("🔑 사용자 식별 - userId: {}", userId);

            List<MoodReport> moodReports = moodReportService.getMoodReportsByUserId(userId);
            log.debug("📝 사용자 무드리포트 {}개 조회됨", moodReports.size());

            List<Long> weatherIds = moodReports.stream()
                    .map(MoodReport::getWeatherId)
                    .collect(Collectors.toList());
            log.debug("🌦 기존 날씨 ID 추출 완료 - {}개", weatherIds.size());

            List<Long> filteredWeatherIds = weatherApiService.getSimilarWeatherIdsFromIds(
                    weatherIds, request.temp_feels_like(), request.rain_amount());
            log.debug("🔍 필터링된 날씨 ID - {}개", filteredWeatherIds.size());

            List<MoodReport> filteredMoodReports =
                    moodReportService.getMoodReportsByWeatherIdsAndUserId(filteredWeatherIds, userId);
            log.debug("🎯 조건에 부합하는 무드리포트 {}개 추출 완료", filteredMoodReports.size());

            StylingSuggestionResponseDTO data = stylingService.StylingSuggestion(filteredMoodReports, request.temp_feels_like());
            log.info("✅ 코디 추천 성공 - 추천 결과 생성됨");

            return ResponseEntity.ok(ApiResponse.success("코디 추천을 완료했습니다.", data));
        } catch (Exception e) {
            log.error("❌ 코디 추천 실패 - reason: {}", e.getMessage(), e);
            throw e;
        }
    }
}
