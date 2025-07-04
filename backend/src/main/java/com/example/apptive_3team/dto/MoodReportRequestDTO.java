package com.example.apptive_3team.dto;

import org.hibernate.type.descriptor.jdbc.TinyIntJdbcType;

import java.time.LocalDate;

public record MoodReportRequestDTO(Long id,
                                   Long weatherId,
                                   LocalDate created_at,
                                   Double latitude,
                                   Double longitude,
                                   String img_top,
                                   String img_bottom,
                                   String img_etc,
                                   String content,
                                   Double score_feel,
                                   TinyIntJdbcType score_icon,
                                   LocalDate date) {
}
