package com.example.apptive_3team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MoodReportRequestDTO(Long id,
                                   Long weatherId,
                                   @NotNull LocalDate date,
                                   @NotNull LocalDate created_at,
                                   @NotNull Double latitude,
                                   @NotNull Double longitude,
                                   @NotBlank String img_top,
                                   @NotBlank String img_bottom,
                                   @NotBlank String img_etc,
                                   String content,
                                   @NotNull Double score_feel) {
}
