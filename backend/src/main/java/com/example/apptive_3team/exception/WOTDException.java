package com.example.apptive_3team.exception;

import org.springframework.http.HttpStatus;

public class WOTDException extends RuntimeException{
    private final ErrorCode errorCode;
    private final HttpStatus statusCode;

    public WOTDException(ErrorCode errorCode, HttpStatus statusCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public ErrorCode getErrorCode() { return this.errorCode; }

    public int getStatusCode(){
        return errorCode.getStatus().value();
    }

}