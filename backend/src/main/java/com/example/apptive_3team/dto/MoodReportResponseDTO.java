package com.example.apptive_3team.dto;

import com.example.apptive_3team.entity.MoodReport;
import com.example.apptive_3team.entity.WeatherData;

public record MoodReportResponseDTO(WeatherData weatherData,
                                    MoodReport moodReport) {

}
