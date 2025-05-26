package com.example.apptive_3team.controller;

import com.example.apptive_3team.apiResponse.ApiResponse;
import com.example.apptive_3team.apiResponse.ErrorResponse;
import com.example.apptive_3team.dto.WeatherDataDTO;
import com.example.apptive_3team.dto.WeatherRequestDTO;
import com.example.apptive_3team.service.WeatherApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/weather/request")
public class WeatherAPIController {
    @Autowired
    private WeatherApiService weatherApiService;

    @PostMapping
    public ResponseEntity<?> getWeather(@RequestBody WeatherRequestDTO request) {
        try {
            List<WeatherDataDTO> data = weatherApiService.getWeatherData(request.latitude(), request.longitude());
            return ResponseEntity.ok(new ApiResponse<>("날씨 데이터를 성공적으로 불러왔습니다.", data));
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("날씨 데이터를 불러오는 중 오류가 발생했습니다.", "GET_WEATHER_API_ERROR"));
        }
    }
}