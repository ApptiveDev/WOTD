package com.example.apptive_3team.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

import java.time.LocalDate;

public record MoodReportRequestDTO(Long id,
                                   Long weatherId,
                                   @NotBlank LocalDate date,
                                   @NotBlank LocalDate created_at,
                                   @NotBlank Double latitude,
                                   @NotBlank Double longitude,
                                   @NotBlank String img_top,
                                   @NotBlank String img_bottom,
                                   @NotBlank String img_etc,
                                   String content,
                                   @NotBlank Double score_feel,
                                   @NotBlank TinyIntJdbcType score_icon) {
}
