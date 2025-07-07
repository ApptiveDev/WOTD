package com.example.apptive_3team.exception.WeatherData;

import com.example.apptive_3team.exception.ErrorCode;
import com.example.apptive_3team.exception.WOTDException;
import org.springframework.http.HttpStatus;

public class GetWeatherApiException extends WOTDException {
    public GetWeatherApiException() {
        super(ErrorCode.GET_WEATHER_API_ERROR, HttpStatus.BAD_REQUEST);
    }
}
