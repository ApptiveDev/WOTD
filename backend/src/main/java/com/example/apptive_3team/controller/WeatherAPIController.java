package com.example.apptive_3team.controller;

import com.example.apptive_3team.ApiResponse;
import com.example.apptive_3team.dto.WeatherDataDTO;
import com.example.apptive_3team.dto.WeatherRequestDTO;
import com.example.apptive_3team.service.WeatherApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/weather/request")
public class WeatherAPIController {
    @Autowired
    private WeatherApiService weatherApiService;

    @PostMapping
    public ResponseEntity<?> getWeather(@RequestBody WeatherRequestDTO request) {
        log.info("📥 [POST] weather/request API 호출됨");
        
        WeatherDataDTO data = weatherApiService.getWeatherData(request.date(), request.latitude(), request.longitude());

        return ResponseEntity.ok(ApiResponse.success("날씨 데이터를 성공적으로 불러왔습니다.", data));
    }
}