package com.example.apptive_3team.dto;

import jakarta.validation.constraints.NotNull;

public record StylingSuggestionRequestDTO(@NotNull Double temp_feels_like,
                                          @NotNull Double rain_amount) {
}
