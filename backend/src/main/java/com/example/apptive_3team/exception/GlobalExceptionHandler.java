package com.example.apptive_3team.exception;

import com.example.apptive_3team.exception.Item.ItemNotFoundException;
import com.example.apptive_3team.exception.KakaoLogin.InvalidKakaoAccessTokenException;
import com.example.apptive_3team.exception.KakaoLogin.JwtValidationException;
import com.example.apptive_3team.exception.MoodReport.MoodReportNotFoundException;
import com.example.apptive_3team.exception.WeatherData.GetWeatherApiException;
import com.example.apptive_3team.exception.WeatherData.NotSupportedLocationException;
import com.example.apptive_3team.exception.WeatherData.NotSupportedDateException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.example.apptive_3team.ApiResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 커스텀 예외 처리
     */
    @ExceptionHandler(WOTDException.class)
    public ResponseEntity<ApiResponse<?>> handleWOTDException(WOTDException ex) {
        log.error("🔥 [예외 발생] - code: {}, message: {}", ex.getErrorCode(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode().name()));
    }

    /**
     * 유효하지 않은 챙길 물품 조회
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleItemNotFoundException(ItemNotFoundException ex) {
        log.error("🔥 [예외 발생] - code: {}, message: {}", ex.getErrorCode(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode().name()));
    }

    /**
     * 알 수 없는 위치 정보
     */
    @ExceptionHandler(NotSupportedLocationException.class)
    public ResponseEntity<ApiResponse<?>> handleNotSupportedLocationException(NotSupportedLocationException ex) {
        log.error("🔥 [예외 발생] - code: {}, message: {}", ex.getErrorCode(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode().name()));
    }

    /**
     * 알 수 없는 날짜 정보
     */
    @ExceptionHandler(NotSupportedDateException.class)
    public ResponseEntity<ApiResponse<?>> handleNotSupportedDateException(NotSupportedDateException ex) {
        log.error("🔥 [예외 발생] - code: {}, message: {}", ex.getErrorCode(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode().name()));
    }

    /**
     * 날씨 조회
     */
    @ExceptionHandler(GetWeatherApiException.class)
    public ResponseEntity<ApiResponse<?>> handleGetWeatherApiException(GetWeatherApiException ex) {
        log.error("🔥 [예외 발생] - code: {}, message: {}", ex.getErrorCode(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode().name()));
    }

    /**
     * 카카오 액세스 토큰 유효성 검사
     */
    @ExceptionHandler(InvalidKakaoAccessTokenException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidKakaoAccessTokenException(InvalidKakaoAccessTokenException ex) {
        log.error("🔥 [예외 발생] - code: {}, message: {}", ex.getErrorCode(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode().name()));
    }

    /**
     * 유효하지 않은 무드리포트 조회
     */
    @ExceptionHandler(MoodReportNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleMoodReportNotFoundException(MoodReportNotFoundException ex) {
        log.error("🔥 [예외 발생] - code: {}, message: {}", ex.getErrorCode(), ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(ex.getMessage(), ex.getErrorCode().name()));
    }

    /**
     * http 메소드 요청 오류
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("🔥 [예외 발생] - message: {}", ex.getMessage(), ex);

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(errorMessage, "VALIDATION_ERROR"));
    }

    /**
     *
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("🔥 [예외 발생] - message: {}", ex.getMessage(), ex);

        String errorMessage = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(errorMessage, "VALIDATION_ERROR"));
    }

    /**
     * 파라미터 miss
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingParamException(MissingServletRequestParameterException ex) {
        log.error("🔥 [예외 발생] - message: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error("필수 요청 파라미터가 없습니다: " + ex.getParameterName(), "MISSING_PARAMETER"));
    }

    /**
     * 요청 형식 검사
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        log.error("🔥 [예외 발생] - message: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error("요청 형식이 잘못되었습니다.", "INVALID_JSON_FORMAT"));
    }

    /**
     * 접근 권한 검사
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleExccessDeniedException(AccessDeniedException ex) {
        log.error("🔥 [예외 발생] - message: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error(ex.getMessage(), "ACCESS_DENIED"));
    }

    /**
     * 명시되지 않은 에러 검사
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllUnhandled(Exception ex, HttpServletRequest request) {
        log.error("❗ [Global] [{} {}] 처리되지 않은 예외 - URI: {}, 에러: {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.error("예상치 못한 오류가 발생했습니다.", "INTERNAL_ERROR"));
    }

    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtValidation(JwtValidationException ex, HttpServletRequest request) {
        log.error("❗ [Global] [{} {}] 인증 실패 - URI: {}, 에러: {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("인증 실패 : ", ex.getMessage()));
    }

}
