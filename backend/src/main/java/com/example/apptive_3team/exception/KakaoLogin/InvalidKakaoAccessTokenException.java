package com.example.apptive_3team.exception.KakaoLogin;

import com.example.apptive_3team.exception.ErrorCode;
import com.example.apptive_3team.exception.WOTDException;
import org.springframework.http.HttpStatus;

public class InvalidKakaoAccessTokenException extends WOTDException {
    public InvalidKakaoAccessTokenException()
    {
        super(ErrorCode.INVALID_KAKAO_ACCESS_TOKEN_ERROR, HttpStatus.BAD_REQUEST);
    }
}
