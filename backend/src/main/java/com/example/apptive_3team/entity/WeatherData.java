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
    private double temp_feels_like;
    private double temp_min;
    private double temp_max;
    private double temp_avg;
    private double rain_amount;

    public WeatherData() {}

    public WeatherData(LocalDate date, double temp_feels_like, double temp_min, double temp_max, double temp_avg, double rain_amount) {
        this.date = date;
        this.temp_feels_like = temp_feels_like;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.temp_avg = temp_avg;
        this.rain_amount = rain_amount;
    }

    @Override
    public int compareTo(WeatherData other) {
        return this.date.compareTo(other.date); // 문자열로 날짜 비교 (yyyy-MM-dd 형식이면 이대로 정렬 가능)
    }
}

