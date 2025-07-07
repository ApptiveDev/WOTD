package com.example.apptive_3team.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class MoodReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id") // DB 컬럼명은 user_id로 매핑
    private Long userId;

    @Column(name = "weather_id") // DB 컬럼명은 weather_id로 매핑
    private Long weatherId;

    private LocalDate date;
    private LocalDate created_at;
    private Double latitude;
    private Double longitude;
    private String img_top;
    private String img_bottom;
    private String img_etc;
    private String content;
    private Double score_feel;

    public MoodReport() {}

}
