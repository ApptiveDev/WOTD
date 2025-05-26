package com.example.apptive_3team.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "weather.api")
@Getter
@Setter
public class WeatherApiConfig {
    private String key;
    private String url;
}
