package com.example.apptive_3team.apiResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({ "isSuccess", "message", "data" })
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private boolean isSuccess;

    private String message;
    private T data;

    public ApiResponse(String message, T data) {
        this.isSuccess = true;
        this.message = message;
        this.data = data;
    }
}
