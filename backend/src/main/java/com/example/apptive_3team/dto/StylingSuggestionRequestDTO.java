package com.example.apptive_3team.dto;

import jakarta.validation.constraints.NotBlank;

public record StylingSuggestionRequestDTO(@NotBlank Double temp_feels_like,
                                          @NotBlank Double rain_amount) {
}
