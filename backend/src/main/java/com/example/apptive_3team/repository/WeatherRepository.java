package com.example.apptive_3team.repository;

import com.example.apptive_3team.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherData, Long> {

    /**
     * 날짜 및 위도, 경도 정보에 해당하는 날짜 정보를 DB에서 조회하는 메서드.
     *
     * @param date 날짜
     * @param latitude 위도
     * @param longitude 경도
     */
    @Query("SELECT w FROM WeatherData w WHERE w.date = :date AND w.latitude = :lat AND w.longitude = :lon")
    Optional<WeatherData> findByDateAndLocation(@Param("date") LocalDate date,
                                                @Param("lat") Double latitude,
                                                @Param("lon") Double longitude);


    /**
     * 체감온도를 기준으로 최소값~최대값 사이의 온도를 가지면서,
     * 강수량을 기준으로 3가지 단계로 튜플들을 분류하고, 각 단계에 해당하는 튜플들을 조회
     *
     * @param ids 날씨 ID 값들
     * @param min 체감온도 범위 최소값
     * @param max 체감온도 범위 최대값
     * @param range 강수량 레벨.
     *      *              ZERO: rain_amount = 0,
     *      *              LOW: 0 < rain_amount <= 3,
     *      *              HIGH: 3 < rain_amount
     * <p>호출 예시
     * <p>
     * <p>double tempTarget = 22.0;
     * <p>double tempRange = 1.5;
     * <p>String rainLevel = "LOW";
     *
     * <p>List<WeatherData> results = weatherRepository.findByTempFeelsLikeAndRainAmountRange(
     *     tempTarget - tempRange,
     *     tempTarget + tempRange,
     *     rainLevel
     * );
     */
    @Query("""
SELECT w.id FROM WeatherData w
WHERE 
  w.id IN :ids
  AND w.temp_feels_like BETWEEN :min AND :max
  AND (
    (:range = 'ZERO' AND w.rain_amount = 0)
    OR (:range = 'LOW' AND w.rain_amount > 0 AND w.rain_amount <= 3)
    OR (:range = 'HIGH' AND w.rain_amount > 3)
  )
""")
    List<Long> findSimilarWeatherIdsFromIds(
            @Param("ids") List<Long> ids,
            @Param("min") double min,
            @Param("max") double max,
            @Param("range") String range
    );

}