package com.example.apptive_3team.service;

import com.example.apptive_3team.config.WeatherApiConfig;
import com.example.apptive_3team.dto.WeatherDataDTO;
import com.example.apptive_3team.entity.WeatherData;
import com.example.apptive_3team.exception.WeatherData.GetWeatherApiException;
import com.example.apptive_3team.exception.WeatherData.NotSupportedDateException;
import com.example.apptive_3team.repository.WeatherRepository;
import com.example.apptive_3team.util.DateUtils;
import com.example.apptive_3team.util.NumberUtils;
import jakarta.transaction.Transactional;
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
public class WeatherApiService {
    private final WeatherRepository weatherRepository;
    private final WeatherApiConfig weatherApiConfig;
    private final DateUtils dateUtils;
    private final NumberUtils numberUtils;

    public WeatherApiService(WeatherRepository weatherRepository,
                             WeatherApiConfig weatherApiConfig,
                             DateUtils dateUtils,
                             NumberUtils numberUtils) {

        this.weatherRepository = weatherRepository;
        this.weatherApiConfig = weatherApiConfig;
        this.dateUtils = dateUtils;
        this.numberUtils = numberUtils;
    }

    /**
     * date가 과거인지 미래인지에 따라 과거/예보 날씨 정보를 적절하게 수행하는 메서드.
     *
     * @param date 날짜
     * @param latitude 위도
     * @param longitude 경도
     * @return WeatherDataDTO
     */
    public WeatherDataDTO getWeatherData(LocalDate date, double latitude, double longitude) {
        LocalDate today = LocalDate.now();

        try {
            if (date.isBefore(today)) {
                return getPastWeatherData(date, latitude, longitude);
            } else {
                return getForecastWeatherData(date, latitude, longitude);
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
     * @return WeatherDateDTO
     * @throws IOException
     */
    public WeatherDataDTO getForecastWeatherData(LocalDate date, double latitude, double longitude) {

        try {
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

                double tempMin = temp.getDouble("min");
                double tempMax = temp.getDouble("max");

                double tempAvg = numberUtils.average(
                        temp.getDouble("day"),
                        temp.getDouble("night"),
                        temp.getDouble("eve"),
                        temp.getDouble("morn")
                );

                double feelsLikeAvg = numberUtils.average(
                        feelsLike.getDouble("day"),
                        feelsLike.getDouble("night"),
                        feelsLike.getDouble("eve"),
                        feelsLike.getDouble("morn")
                );

                double rainAmount = 0.0;
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

                WeatherData weatherData = new WeatherData(forecastDate, feelsLikeAvg, tempMin, tempMax, tempAvg, rainAmount, description);
                saveOrUpdateWeather(weatherData);

                return new WeatherDataDTO(forecastDate, feelsLikeAvg, tempMin, tempMax, tempAvg, rainAmount, description);
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
     * @return WeatherDataDTO
     * @throws IOException
     */
    public WeatherDataDTO getPastWeatherData(LocalDate date, double latitude, double longitude) {

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

            double temp_min = Double.MAX_VALUE;
            double temp_max = Double.MIN_VALUE;
            List<Double> tempList = new ArrayList<>();
            List<Double> feelsLikeList = new ArrayList<>();
            List<Double> rainList = new ArrayList<>();
            Map<String, Integer> descriptionCountMap = new HashMap<>();

            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                JSONObject main = item.getJSONObject("main");

                double temp = main.getDouble("temp");
                double feels_like = main.getDouble("feels_like");
                double tMin = main.getDouble("temp_min");
                double tMax = main.getDouble("temp_max");

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

            double temp_avg = numberUtils.average(tempList.stream().mapToDouble(Double::doubleValue).toArray());
            double feels_like_avg = numberUtils.average(feelsLikeList.stream().mapToDouble(Double::doubleValue).toArray());
            double rain_avg = rainList.isEmpty() ? 0.0 :
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

            WeatherData weatherData = new WeatherData(date, feels_like_avg, temp_min, temp_max, temp_avg, rain_avg, mostCommonDescription);
            saveOrUpdateWeather(weatherData);

            return new WeatherDataDTO(date, feels_like_avg, temp_min, temp_max, temp_avg, rain_avg, mostCommonDescription);
        } catch (IOException e) {
            throw new RuntimeException("날씨 데이터를 가져오는 데 실패했습니다.", e);
        }
    }

    /**
     * 날씨정보의 DB저장 유무에 관계없이 date에 해당하는 날씨 Id를 조회하는 메서드.
     *
     * @param date 날짜
     * @param lat 위도
     * @param lon 경도
     * @return
     */
    public Long getWeatherId(LocalDate date, Double lat, Double lon) {
        Optional<WeatherData> weather = weatherRepository.findByDate(date)
                .or(() -> {
                    // DB에 없으면 외부 API로 날씨정보 저장 및 조회
                    getWeatherData(date, lat, lon);
                    return weatherRepository.findByDate(date);
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
        weatherRepository.findByDate(newWeatherData.getDate())
                .ifPresent(existingWeather -> {
                    newWeatherData.setId(existingWeather.getId()); // ID 같게 설정해서 덮어쓰기
                });

        weatherRepository.save(newWeatherData); // 새 데이터거나 기존 ID면 update됨
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