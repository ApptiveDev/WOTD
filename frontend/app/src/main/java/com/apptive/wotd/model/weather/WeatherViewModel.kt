package com.apptive.wotd.model.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherApi: WeatherApi
) : ViewModel() {

    private val TAG = "WeatherViewModel"

    private val _weather = MutableStateFlow<WeatherData?>(null)
    val weather: StateFlow<WeatherData?> = _weather

    fun fetchWeather(lat: Double, lon: Double, date: LocalDate) {
        _weather.value = null
        
        viewModelScope.launch {
            try {
                Log.d(TAG, "날씨 API 호출 시작 - lat: $lat, lon: $lon, date: $date")
                
                val request = WeatherRequest(
                    date = date.toString(),
                    latitude = lat,
                    longitude = lon
                )
                Log.d(TAG, "요청 데이터: $request")
                
                val response = weatherApi.getWeather(request)
                Log.d(TAG, "API 응답: $response")
                
                if (response.isSuccess) {
                    val weatherData = response.data
                    Log.d(TAG, "날씨 데이터 성공적으로 받아옴: $weatherData")
                    _weather.value = weatherData
                } else {
                    Log.e(TAG, "API 응답 실패 - isSuccess: ${response.isSuccess}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "날씨 API 호출 중 오류 발생", e)
                e.printStackTrace()
            }
        }
    }

    fun clearWeather() {
        _weather.value = null
    }
}