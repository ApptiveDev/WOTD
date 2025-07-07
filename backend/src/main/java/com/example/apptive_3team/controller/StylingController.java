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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

        Long user_id = kakaoService.getUserIdFromJwtToken(token);
        List<MoodReport> moodReports = moodReportService.getMoodReportsByUserId(user_id);
        List<Long> weatherIds = moodReports.stream()
                .map(MoodReport::getWeatherId)
                .collect(Collectors.toList());
        List<Long> filteredWeatherIds = weatherApiService.getSimilarWeatherIdsFromIds(weatherIds, request.temp_feels_like(), request.rain_amount());
        List<MoodReport> filteredMoodReports = moodReportService.getMoodReportsByWeatherIdsAndUserId(filteredWeatherIds, user_id);
        StylingSuggestionResponseDTO data = stylingService.StylingSuggestion(filteredMoodReports);

        return ResponseEntity.ok(ApiResponse.success("코디 추천을 완료했습니다.", data));
    }
}
