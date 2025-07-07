package com.example.apptive_3team.dto;

import java.time.LocalDate;

public record WeatherDataDTO(LocalDate date,
                             Double tempFeelsLike,
                             Double tempMin,
                             Double tempMax,
                             Double tempAvg,
                             Double rainAmount,
                             String description,
                             Double latitude,
                             Double longitude) {
}