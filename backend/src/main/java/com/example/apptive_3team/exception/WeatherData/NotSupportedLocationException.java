package com.example.apptive_3team.exception.WeatherData;

import com.example.apptive_3team.exception.ErrorCode;
import com.example.apptive_3team.exception.WOTDException;
import org.springframework.http.HttpStatus;

public class NotSupportedLocationException extends WOTDException {
    public NotSupportedLocationException() {
        super(ErrorCode.NOT_SUPPORTED_LOCATION_ERROR, HttpStatus.BAD_REQUEST);
    }
}
