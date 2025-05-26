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

    @Query("SELECT w FROM WeatherData w WHERE w.temp_feels_like BETWEEN :min AND :max")
    List<WeatherData> findByTempFeelsLikeRange(@Param("min") double min, @Param("max") double max);
    //  호출 예시
    //  double target = 22.0;
    //  double range = 1.5;
    //  repository.findByTempFeelsLikeRange(target - range, target + range);

    @Query("""
    SELECT w FROM WeatherData w
    WHERE
      (:range = 'ZERO' AND w.rain_amount = 0)
      OR (:range = 'LOW' AND w.rain_amount > 0 AND w.rain_amount <= 3)
      OR (:range = 'HIGH' AND w.rain_amount > 3)
    """)
    List<WeatherData> findByRainAmountRange(@Param("range") String range);
    // 호출 예시
    // repository.findByRainAmountRange("ZERO");
}
