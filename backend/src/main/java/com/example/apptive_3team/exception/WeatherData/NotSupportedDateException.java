package com.example.apptive_3team.exception.WeatherData;

import com.example.apptive_3team.exception.ErrorCode;
import com.example.apptive_3team.exception.WOTDException;
import org.springframework.http.HttpStatus;

public class NotSupportedDateException extends WOTDException {
  public NotSupportedDateException() {
    super(ErrorCode.NOT_SUPPORTED_DATE_ERROR, HttpStatus.BAD_REQUEST);
  }
}

