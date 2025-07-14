package com.example.apptive_3team.exception.KakaoLogin;

import com.example.apptive_3team.exception.ErrorCode;
import com.example.apptive_3team.exception.WOTDException;

public class JwtValidationException extends WOTDException {

  public JwtValidationException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, errorCode.getStatus());
    initCause(cause);
  }
}