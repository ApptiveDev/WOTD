package com.example.apptive_3team.dto;

import java.time.LocalDate;

public record WeatherDataDTO(LocalDate date,
                             double tempFeelsLike,
                             double tempMin,
                             double tempMax,
                             double tempAvg,
                             double rainAmount) {
}