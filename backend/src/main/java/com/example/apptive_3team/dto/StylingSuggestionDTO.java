package com.example.apptive_3team.dto;

import java.time.LocalDate;

public record StylingSuggestionDTO(Long moodReportId,
                                   Long weatherId,
                                   LocalDate date,
                                   String img_top,
                                   String img_bottom,
                                   String img_etc,
                                   Double score_feel) {
}
