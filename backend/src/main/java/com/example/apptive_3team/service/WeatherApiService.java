package com.example.apptive_3team.service;

import com.example.apptive_3team.config.WeatherApiConfig;
import com.example.apptive_3team.dto.WeatherDataDTO;
import com.example.apptive_3team.dto.WeatherSummaryDTO;
import com.example.apptive_3team.entity.WeatherData;
import com.example.apptive_3team.exception.WeatherData.GetWeatherApiException;
import com.example.apptive_3team.exception.WeatherData.NotSupportedDateException;
import com.example.apptive_3team.repository.WeatherRepository;
import com.example.apptive_3team.util.DateUtils;
import com.example.apptive_3team.util.NumberUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WeatherApiService {
    private final WeatherRepository weatherRepository;
    private final WeatherApiConfig weatherApiConfig;
    private final DateUtils dateUtils;
    private final NumberUtils numberUtils;

    /**
     * date가 과거인지 미래인지에 따라 과거/예보 날씨 정보를 적절하게 수행하는 메서드.
     *
     * @param date 날짜
     * @param latitude 위도
     * @param longitude 경도
     */
    public WeatherDataDTO getWeatherData(LocalDate date, Double latitude, Double longitude) {
        LocalDate today = LocalDate.now();
        Double lat = numberUtils.round(latitude);
        Double lon = numberUtils.round(longitude);

        try {
            if (date.isBefore(today)) {
                return getPastWeatherData(date, lat, lon);
            } else {
                return getForecastWeatherData(date, lat, lon);
            }
        } catch (GetWeatherApiException e) {
            throw e;
        }
    }

    /**
     * 예보 데이터를 반환하는 API 요청 메서드.
     *
     * @param date 날짜
     * @param latitude 위도
     * @param longitude 경도
     */
    public WeatherDataDTO getForecastWeatherData(LocalDate date, Double latitude, Double longitude) {

        try {
            validateDateIsUnder30(date);

            StringBuilder urlBuilder = new StringBuilder(weatherApiConfig.getForecastUrl());
            urlBuilder.append("?&lat=").append(latitude);
            urlBuilder.append("&lon=").append(longitude);
            urlBuilder.append("&appid=").append(URLEncoder.encode(weatherApiConfig.getKey(), "UTF-8"));
            urlBuilder.append("&cnt=30");
            urlBuilder.append("&units=metric");

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            int responseCode = conn.getResponseCode();
            validateApiCallIsAlright(responseCode); // 200~300이 아니면 예외 발생

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            JSONObject json = new JSONObject(sb.toString());
            JSONArray list = json.getJSONArray("list");

            // 응답의 날짜들을 순회하며 targetDate에 해당하는 것만 필터링
            for (int i = 0; i < list.length(); i++) {
                JSONObject dayForecast = list.getJSONObject(i);

                long timestamp = dayForecast.getLong("dt");
                LocalDate forecastDate = Instant.ofEpochSecond(timestamp)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();

                if (!forecastDate.equals(date)) {
                    continue; // targetDate가 아닌 날은 건너뜀
                }

                JSONObject temp = dayForecast.getJSONObject("temp");
                JSONObject feelsLike = dayForecast.getJSONObject("feels_like");
                JSONArray weatherArray = dayForecast.getJSONArray("weather");

                Double tempMin = temp.getDouble("min");
                Double tempMax = temp.getDouble("max");

                Double tempAvg = numberUtils.average(
                        temp.getDouble("day"),
                        temp.getDouble("night"),
                        temp.getDouble("eve"),
                        temp.getDouble("morn")
                );

                Double feelsLikeAvg = numberUtils.average(
                        feelsLike.getDouble("day"),
                        feelsLike.getDouble("night"),
                        feelsLike.getDouble("eve"),
                        feelsLike.getDouble("morn")
                );

                Double rainAmount = 0.0;
                if (dayForecast.has("rain")) {
                    rainAmount = dayForecast.getDouble("rain");
                }

                String description = null;
                if (weatherArray.length() > 0) {
                    JSONObject weatherObj = weatherArray.getJSONObject(0);
                    if (weatherObj.has("description")) {
                        description = weatherObj.getString("description");
                        // 사용: descriptionCountMap.put(description, ...)
                    }
                }

                // 반올림 처리
                tempAvg = numberUtils.round(tempAvg);
                feelsLikeAvg = numberUtils.round(feelsLikeAvg);
                rainAmount = numberUtils.round(rainAmount);

                WeatherData weatherData = new WeatherData(forecastDate, feelsLikeAvg, tempMin, tempMax, tempAvg, rainAmount, description, latitude, longitude);
                saveOrUpdateWeather(weatherData);

                return new WeatherDataDTO(forecastDate, feelsLikeAvg, tempMin, tempMax, tempAvg, rainAmount, description, latitude, longitude);
            }

            // 루프를 끝까지 돌았지만 원하는 날짜를 찾지 못한 경우
            throw new NotSupportedDateException();

        } catch (IOException e) {
            throw new RuntimeException("날씨 데이터를 가져오는 데 실패했습니다.", e);
        }
    }

    /**
     * 과거 날씨 데이터를 반환하는 API 요청 메서드.
     *
     * @param date 날짜
     * @param latitude 위도
     * @param longitude 경도
     */
    public WeatherDataDTO getPastWeatherData(LocalDate date, Double latitude, Double longitude) {

        try {
            long unixTime = dateUtils.convertDateToUnix(date) - 43200;

            StringBuilder urlBuilder = new StringBuilder(weatherApiConfig.getPastUrl());
            urlBuilder.append("?&lat=").append(latitude);
            urlBuilder.append("&lon=").append(longitude);
            urlBuilder.append("&type=hour");
            urlBuilder.append("&units=metric");
            urlBuilder.append("&start=").append(unixTime);
            urlBuilder.append("&cnt=24");
            urlBuilder.append("&appid=").append(URLEncoder.encode(weatherApiConfig.getKey(), "UTF-8"));

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            int responseCode = conn.getResponseCode();
            validateApiCallIsAlright(responseCode); // 200~300이 아니면 예외 발생

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            JSONObject json = new JSONObject(sb.toString());
            JSONArray list = json.getJSONArray("list");

            Double temp_min = Double.MAX_VALUE;
            Double temp_max = Double.MIN_VALUE;
            List<Double> tempList = new ArrayList<>();
            List<Double> feelsLikeList = new ArrayList<>();
            List<Double> rainList = new ArrayList<>();
            Map<String, Integer> descriptionCountMap = new HashMap<>();

            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                JSONObject main = item.getJSONObject("main");

                Double temp = main.getDouble("temp");
                Double feels_like = main.getDouble("feels_like");
                Double tMin = main.getDouble("temp_min");
                Double tMax = main.getDouble("temp_max");

                temp_min = Math.min(temp_min, tMin);
                temp_max = Math.max(temp_max, tMax);
                tempList.add(temp);
                feelsLikeList.add(feels_like);

                if (item.has("rain")) {
                    JSONObject rainObj = item.getJSONObject("rain");
                    if (rainObj.has("1h")) {
                        rainList.add(rainObj.getDouble("1h"));
                    }
                }

                if (item.has("weather")) {
                    JSONArray weatherArray = item.getJSONArray("weather");
                    if (weatherArray.length() > 0) {
                        String desc = weatherArray.getJSONObject(0).getString("description");
                        descriptionCountMap.put(desc, descriptionCountMap.getOrDefault(desc, 0) + 1);
                    }
                }
            }

            Double temp_avg = numberUtils.average(tempList.stream().mapToDouble(Double::doubleValue).toArray());
            Double feels_like_avg = numberUtils.average(feelsLikeList.stream().mapToDouble(Double::doubleValue).toArray());
            Double rain_avg = rainList.isEmpty() ? 0.0 :
                    numberUtils.average(rainList.stream().mapToDouble(Double::doubleValue).toArray());

            // 반올림
            temp_avg = numberUtils.round(temp_avg);
            feels_like_avg = numberUtils.round(feels_like_avg);
            rain_avg = numberUtils.round(rain_avg);
            temp_min = numberUtils.round(temp_min);
            temp_max = numberUtils.round(temp_max);

            // 가장 자주 등장한 description 선정
            String mostCommonDescription = descriptionCountMap.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("정보 없음");

            WeatherData weatherData = new WeatherData(date, feels_like_avg, temp_min, temp_max, temp_avg, rain_avg, mostCommonDescription, latitude, longitude);
            saveOrUpdateWeather(weatherData);

            return new WeatherDataDTO(date, feels_like_avg, temp_min, temp_max, temp_avg, rain_avg, mostCommonDescription, latitude, longitude);
        } catch (IOException e) {
            throw new RuntimeException("날씨 데이터를 가져오는 데 실패했습니다.", e);
        }
    }

    /**
     * 날씨정보의 DB저장 유무에 관계없이 date에 해당하는 날씨 Id를 조회하는 메서드.
     *
     * @param date 날짜
     * @param latitude 위도
     * @param longitude 경도
     * @return
     */
    public Long getWeatherId(LocalDate date, Double latitude, Double longitude) {
        Double lat = numberUtils.round(latitude);
        Double lon = numberUtils.round(longitude);

        Optional<WeatherData> weather = weatherRepository.findByDateAndLocation(date, lat, lon)
                .or(() -> {
                    // DB에 없으면 외부 API로 날씨정보 저장 및 조회
                    getWeatherData(date, lat, lon);
                    return weatherRepository.findByDateAndLocation(date, lat, lon);
                });

        return weather.get().getId();
    }


    /**
     * DB에 날씨 정보가 존재하지 않으면 저장, 존재하면 덮어쓰는 메서드.
     *
     * @param newWeatherData
     */
    @Transactional
    public void saveOrUpdateWeather(WeatherData newWeatherData) {
        weatherRepository.findByDateAndLocation(newWeatherData.getDate(), newWeatherData.getLatitude(), newWeatherData.getLongitude())
                .ifPresent(existingWeather -> {
                    newWeatherData.setId(existingWeather.getId()); // ID 같게 설정해서 덮어쓰기
                });

        weatherRepository.save(newWeatherData); // 새 데이터거나 기존 ID면 update됨
    }

    /**
     * ID값으로 날씨데이터를 DB에서 조회하는 메서드
     *
     * @param id 날씨데이터 ID
     */
    public WeatherDataDTO getWeatherDataById(Long id) {
        return weatherRepository.findById(id)
                .map(weatherData -> new WeatherDataDTO(
                        weatherData.getDate(),
                        weatherData.getTemp_feels_like(),
                        weatherData.getTemp_min(),
                        weatherData.getTemp_max(),
                        weatherData.getTemp_avg(),
                        weatherData.getRain_amount(),
                        weatherData.getDescription(),
                        weatherData.getLatitude(),
                        weatherData.getLongitude()
                ))
                .orElseThrow(() -> new GetWeatherApiException());
    }

    /**
     * 날씨 ID 리스트 내의 ID 값을 가지는 날씨 정보 중에서, 체감온도와 강수량을 기반으로 비슷한 날씨를 필터링하는 메서드.
     *
     * @param ids 날씨 ID 리스트
     * @param temp 체감 온도
     * @param rainAmount 강수량
     * @return 필터링된 날씨 ID 리스트
     */
    public List<WeatherSummaryDTO> getSimilarWeathersFromIds(List<Long> ids, Double temp, Double rainAmount) {

        double range = 1.5;
        double minTemp = temp - range;
        double maxTemp = temp + range;

        // 강수량 범주 설정
        String rainRange;
        if (rainAmount == 0.0) {
            rainRange = "ZERO";
        } else if (rainAmount <= 3.0) {
            rainRange = "LOW";
        } else {
            rainRange = "HIGH";
        }

        List<WeatherData> weathers = weatherRepository.findSimilarWeathersFromIds(ids, minTemp, maxTemp, rainRange);

        return weathers.stream()
                .map(w -> new WeatherSummaryDTO(
                        w.getId(),
                        w.getTemp_feels_like(),
                        w.getTemp_avg()
                ))
                .toList();
    }

    /**
     * openWeatherApi 호출이 정상적으로 작동됐는지 확인하는 메서드.
     *
     * @param responseCode
     */
    public void validateApiCallIsAlright(int responseCode) {
        if (responseCode < 200 || responseCode > 300) {
            throw new GetWeatherApiException();
        }
    }

    /**
     * 날씨 예보가 최대 30일까지 지원되기에, 30일 이내로 요청한 것인지 확인하는 메서드.
     *
     * <p>주의) 제공받는 예보가 UCT 시간이라서, 한국시간으로는 시간대에 따라 29일 후까지 일 때도 있음.
     *
     * @param date
     */
    public void validateDateIsUnder30(LocalDate date) {
        LocalDate today = LocalDate.now(); // 오늘
        LocalDate limitDate = today.plusDays(30); // 30일 후

        if (date.isAfter(limitDate)) {
            throw new NotSupportedDateException();
        }
    }

}