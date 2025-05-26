package com.example.apptive_3team.service;

import com.example.apptive_3team.config.WeatherApiConfig;
import com.example.apptive_3team.dto.WeatherDataDTO;
import com.example.apptive_3team.entity.WeatherData;
import com.example.apptive_3team.repository.WeatherRepository;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherApiService {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final WeatherRepository weatherRepository;
    private final WeatherApiConfig weatherApiConfig;

    public WeatherApiService(WeatherRepository weatherRepository, WeatherApiConfig weatherApiConfig) {
        this.weatherRepository = weatherRepository;
        this.weatherApiConfig = weatherApiConfig;
    }

    public List<WeatherDataDTO> getWeatherData(double latitude, double longitude) throws IOException {
        StringBuilder urlBuilder = new StringBuilder(weatherApiConfig.getUrl());
        urlBuilder.append("?&lat=").append(latitude);
        urlBuilder.append("&lon=").append(longitude);
        urlBuilder.append("&appid=").append(URLEncoder.encode(weatherApiConfig.getKey(), "UTF-8"));
        urlBuilder.append("&lang=kr");
        urlBuilder.append("&units=metric");

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd = new BufferedReader(new InputStreamReader(
                conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300 ?
                        conn.getInputStream() : conn.getErrorStream()
        ));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        JSONObject json = new JSONObject(sb.toString());
        JSONArray list = json.getJSONArray("list");

        Map<String, List<JSONObject>> groupedByDate = new HashMap<>();
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            String fullDate = item.getString("dt_txt");
            String dateOnly = fullDate.split(" ")[0];
            groupedByDate.computeIfAbsent(dateOnly, k -> new ArrayList<>()).add(item);
        }

        List<WeatherData> entityList = new ArrayList<>();

        for (String dateStr : groupedByDate.keySet()) {
            List<JSONObject> dayItems = groupedByDate.get(dateStr);

            double temp_min = Double.MAX_VALUE;
            double temp_max = Double.MIN_VALUE;
            double temp_sum = 0.0;
            double temp_feels_like_sum = 0.0;
            double rain_amount_sum = 0.0;

            for (JSONObject item : dayItems) {
                JSONObject main = item.getJSONObject("main");

                double temp = main.getDouble("temp");
                double feels_like = main.getDouble("feels_like");
                double tMin = main.getDouble("temp_min");
                double tMax = main.getDouble("temp_max");

                temp_min = Math.min(temp_min, tMin);
                temp_max = Math.max(temp_max, tMax);
                temp_sum += temp;
                temp_feels_like_sum += feels_like;

                if (item.has("rain")) {
                    JSONObject rainObj = item.getJSONObject("rain");
                    if (rainObj.has("1h")) {
                        rain_amount_sum += rainObj.getDouble("1h");
                    }
                }
            }

            int count = dayItems.size();
            double temp_avg = temp_sum / count;
            double temp_feels_like_avg = temp_feels_like_sum / count;

            temp_feels_like_avg = Math.round(temp_feels_like_avg * 100.0) / 100.0;
            temp_avg = Math.round(temp_avg * 100.0) / 100.0;
            rain_amount_sum = Math.round(rain_amount_sum * 100.0) / 100.0;

            LocalDate parsedDate = LocalDate.parse(dateStr, formatter);
            entityList.add(new WeatherData(parsedDate, temp_feels_like_avg, temp_min, temp_max, temp_avg, rain_amount_sum));
        }

        Collections.sort(entityList);
        saveOrUpdateWeather(entityList);

        // Entity → DTO 변환
        return entityList.stream()
                .map(e -> new WeatherDataDTO(
                        e.getDate(),
                        e.getTemp_feels_like(),
                        e.getTemp_min(),
                        e.getTemp_max(),
                        e.getTemp_avg(),
                        e.getRain_amount()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveOrUpdateWeather(List<WeatherData> newWeatherDataList) {
        for (WeatherData newWeatherData : newWeatherDataList) {
            weatherRepository.findByDate(newWeatherData.getDate())
                    .ifPresent(existingWeather -> {
                        newWeatherData.setId(existingWeather.getId()); // ID 같게 설정해서 덮어쓰기
                    });

            weatherRepository.save(newWeatherData); // 새 데이터거나 기존 ID면 update됨
        }
    }
}