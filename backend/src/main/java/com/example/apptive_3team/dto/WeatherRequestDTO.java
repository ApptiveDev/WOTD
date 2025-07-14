package com.example.apptive_3team.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record WeatherRequestDTO(@NotNull LocalDate date,
                                @NotNull Double latitude,
                                @NotNull Double longitude){
}

