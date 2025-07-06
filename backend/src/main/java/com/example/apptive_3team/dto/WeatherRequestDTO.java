package com.example.apptive_3team.dto;

import java.time.LocalDate;

public record WeatherRequestDTO(LocalDate date,
                                Double latitude,
                                Double longitude){
}

