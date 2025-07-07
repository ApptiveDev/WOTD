package com.example.apptive_3team.util;

import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class DateUtils {

    /**
     * 주어진 LocalDate의 UTC 기준 정오(12:00 PM) 시간을 Unix 타임스탬프(초)로 반환하는 메서드.
     *
     * @param date 변환할 날짜 (LocalDate)
     * @return Unix 타임스탬프 (정오 기준, 초 단위, UTC)
     */
    public static long convertDateToUnix(LocalDate date) {
        // UTC 기준 정오 시간 생성
        ZonedDateTime noonUtc = date.atTime(LocalTime.NOON).atZone(ZoneOffset.UTC);
        return noonUtc.toEpochSecond();
    }

    /**
     * UNIX 시간에 해당하는 LocalDate를 구하는 메서드.
     *
     * @param unixTimeUtc 영국의 Unix 시간
     * @return 해당 param에 해당하는 날짜
     */
    public static LocalDate convertUnixToDate(long unixTimeUtc) {
        return Instant.ofEpochSecond(unixTimeUtc) // UTC 기준 Instant 생성
                .atZone(ZoneOffset.UTC)
                .toLocalDate(); // 날짜 부분만 추출
    }

}
