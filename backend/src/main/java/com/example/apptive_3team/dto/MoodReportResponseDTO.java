package com.example.apptive_3team.dto;

import com.example.apptive_3team.entity.MoodReport;

public record MoodReportResponseDTO(WeatherDataDTO weatherData,
                                    MoodReport moodReport) {

}
