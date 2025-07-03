package com.example.apptive_3team.repository;

import com.example.apptive_3team.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherData, Long> {
    Optional<WeatherData> findById (Long id);
    Optional<WeatherData> findByDate (LocalDate date);

    /**
     * 체감온도를 기준으로 최소값~최대값 사이의 온도를 가진 튜플들을 조회
     *
     * @param min 최소 체감온도
     * @param max 최대 체감온도
     *
     * <p>호출 예시
     * <p>double target = 22.0;
     * <p>double range = 1.5;
     * <p>repository.findByTempFeelsLikeRange(target - range, target + range);
     *
     */
    @Query("SELECT w FROM WeatherData w WHERE w.temp_feels_like BETWEEN :min AND :max")
    List<WeatherData> findByTempFeelsLikeRange(@Param("min") double min, @Param("max") double max);

    /**
     * 강수량을 기준으로 3가지 단계로 튜플들을 분류하고, 각 단계에 해당하는 튜플들을 조회
     *
     * @param range 강수량 레벨.
     *              ZERO: rain_amount = 0,
     *              LOW: 0 < rain_amount <= 3,
     *              HIGH: 3 < rain_amount
     * <p>호출 예시
     * <p>repository.findByRainAmountRange("ZERO");
     *
     */
    @Query("""
    SELECT w FROM WeatherData w
    WHERE
      (:range = 'ZERO' AND w.rain_amount = 0)
      OR (:range = 'LOW' AND w.rain_amount > 0 AND w.rain_amount <= 3)
      OR (:range = 'HIGH' AND w.rain_amount > 3)
    """)
    List<WeatherData> findByRainAmountRange(@Param("range") String range);
}