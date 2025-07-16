package com.example.apptive_3team.dto;

import java.time.LocalDate;

public record StylingSuggestionDTO(Long moodReportId,
                                   Long weatherId,
                                   LocalDate date,
                                   Double temp_avg,
                                   Double temp_feels_like,
                                   String img_top,
                                   String img_bottom,
                                   String img_etc,
                                   Double score_feel) {
}
