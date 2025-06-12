package com.example.apptive_3team;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonPropertyOrder({ "isSuccess", "message", "data", "errorCode" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final boolean isSuccess;

    private final String message;
    private final T data;
    private final String errorCode;

    @JsonIgnore
    public boolean getIsSuccess() { // getter를 직접 작성하여 이름 고정
        return isSuccess;
    }

    /**
     * 성공 응답 (data 포함)
     *
     * @param message
     * @param data
     *
     * @return 성공 응답
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .message(message)
                .data(data)
                .errorCode(null)
                .build();
    }

    /**
     * 성공 응답 (data 미포함)
     *
     * @param message
     *
     * @return 성공 응답
     */
    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .isSuccess(true)
                .message(message)
                .data(null)
                .errorCode(null)
                .build();
    }

    /**
     * 실패 응답
     *
     * @param message
     * @param errorCode
     *
     * @return 에러 응답
     */
    public static ApiResponse<Void> error(String message, String errorCode) {
        return ApiResponse.<Void>builder()
                .isSuccess(false)
                .message(message)
                .data(null)
                .errorCode(errorCode)
                .build();
    }
}
