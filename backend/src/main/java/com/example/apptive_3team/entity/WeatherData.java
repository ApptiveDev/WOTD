package com.example.apptive_3team.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class WeatherData implements Comparable<WeatherData>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Double temp_feels_like;
    private Double temp_min;
    private Double temp_max;
    private Double temp_avg;
    private Double rain_amount;
    private String description;
    private Double latitude;
    private Double longitude;

    public WeatherData() {}

    public WeatherData(LocalDate date,
                       Double temp_feels_like,
                       Double temp_min,
                       Double temp_max,
                       Double temp_avg,
                       Double rain_amount,
                       String description,
                       Double latitude,
                       Double longitude) {
        this.date = date;
        this.temp_feels_like = temp_feels_like;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.temp_avg = temp_avg;
        this.rain_amount = rain_amount;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int compareTo(WeatherData other) {
        return this.date.compareTo(other.date); // 문자열로 날짜 비교 (yyyy-MM-dd 형식이면 이대로 정렬 가능)
    }
}

