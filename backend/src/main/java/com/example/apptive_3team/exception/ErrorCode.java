package com.example.apptive_3team.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    ITEM_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "챙길물품이 존재하지 않습니다."),
    USER_ALREADY_HAS_20_ITEMS_ERROR(HttpStatus.BAD_REQUEST, "하루에 등록 가능한 챙길 물품 개수 20개를 넘으셨습니다."),
    NOT_SUPPORTED_LOCATION_ERROR(HttpStatus.BAD_REQUEST, "지원되지 않는 경도, 위도 입니다."),
    NOT_SUPPORTED_DATE_ERROR(HttpStatus.BAD_REQUEST, "예보는 최대 30일까지만 지원합니다."),
    GET_WEATHER_API_ERROR(HttpStatus.BAD_REQUEST, "날씨 데이터를 불러오는 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
