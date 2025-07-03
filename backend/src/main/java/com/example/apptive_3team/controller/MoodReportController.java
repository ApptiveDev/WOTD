package com.example.apptive_3team.controller;

import com.example.apptive_3team.ApiResponse;
import com.example.apptive_3team.dto.ItemRequestDTO;
import com.example.apptive_3team.dto.MoodReportRequestDTO;
import com.example.apptive_3team.entity.MoodReport;
import com.example.apptive_3team.service.KakaoService;
import com.example.apptive_3team.service.MoodReportService;
import com.example.apptive_3team.service.WeatherApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moodReport")
@RequiredArgsConstructor
public class MoodReportController {

    private final MoodReportService moodReportService;
    private final WeatherApiService weatherApiService;
    private final KakaoService kakaoService;

    /**
     * ID를 기반으로 무드 리포트 1개를 조회하는 기능.
     *
     */
    @GetMapping("/request/{moodReportId}")
    public ResponseEntity<?> getMoodReport(@PathVariable Long moodReportId) {

        MoodReport data = moodReportService.getMoodReport(moodReportId);
        return ResponseEntity.ok(ApiResponse.success("무드 리포트 조회를 완료했습니다.", data));
    }

    /**
     * user_id를 기반으로 등록된 모든 무드 리포트를 출력하는 함수
     *
     * @param token
     * 헤더의 토큰 추출하여 user_id 검증
     *
     * @return
     *
     * GET /items/requestAll
     * Authorization: Bearer eyJ0eXAiOiJKV1QiLCJh...
     */
    @GetMapping("/requestAll")
    public ResponseEntity<?> getMoodReports(@RequestHeader("Authorization") String token) {

        Long user_id = kakaoService.getUserIdFromAccessToken(token);

        List<MoodReport> data = moodReportService.getMoodReportsByUserId(user_id);

        return ResponseEntity.ok(ApiResponse.success("무드 리포트 조회를 완료했습니다.", data));
    }

    /**
     * 무드 리포트의 정보를 DB에 저장하는 기능.
     *
     * @param request
     * @return 저장 여부에 대한 통보
     */
    @PostMapping("/add")
    public ResponseEntity<?> addMoodReport(@RequestHeader("Authorization") String token,
                                     @Valid @RequestBody MoodReportRequestDTO request) {

        Long user_id = kakaoService.getUserIdFromAccessToken(token);

        moodReportService.saveMoodReport(user_id, request);
        return ResponseEntity.ok(ApiResponse.success("무드 리포트 등록을 완료했습니다."));
    }

    /**
     * DB에 저장된 무드 리포트의 정보를 수정하는 기능.
     *
     * @param request
     * @return 수정 여부에 대한 통보
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateMoodReport(@RequestHeader("Authorization") String token,
                                        @Valid @RequestBody MoodReportRequestDTO request) {

        Long user_id = kakaoService.getUserIdFromAccessToken(token);
        
        moodReportService.updateMoodReport(user_id, request);

        return ResponseEntity.ok(ApiResponse.success("무드 리포트 수정을 완료했습니다."));
    }

    /**
     * DB에 저장된 무드 리포트의 정보를 삭제하는 기능.
     *
     * @return 삭제 여부에 대한 통보
     */
    @DeleteMapping("/delete/{moodReportId}")
    public ResponseEntity<?> deleteMoodReport(@PathVariable Long moodReportId,
                                        @RequestHeader("Authorization") String token) {

        Long user_id = kakaoService.getUserIdFromAccessToken(token);
        
        moodReportService.deleteMoodReport(user_id, moodReportId);
        return ResponseEntity.ok(ApiResponse.success("무드 리포트 삭제를 완료했습니다."));
    }
}
