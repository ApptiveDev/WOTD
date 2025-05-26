package com.example.apptive_3team.apiResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "isSuccess", "message", "errorCode" })
public class ErrorResponse {

    @JsonProperty("isSuccess")
    private boolean isSuccess;

    private String message;
    private String errorCode;

    public ErrorResponse(String message, String errorCode) {
        this.isSuccess = false;
        this.message = message;
        this.errorCode = errorCode;
    }
}
