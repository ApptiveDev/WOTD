package com.apptive.wotd.view.calender

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptive.wotd.R
import com.apptive.wotd.ui.theme.pretendard
import java.time.LocalDate
import com.apptive.wotd.model.auth.WeatherViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptive.wotd.composable.WidthSpacer

@Composable
fun WeatherCard(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val weather by viewModel.weather.collectAsState()
    val TAG = "WeatherCard"

    LaunchedEffect(Unit) {
        Log.d(TAG, "WeatherCard LaunchedEffect 실행 - API 호출 시작")
        viewModel.fetchWeather(
            lat = 35.228700446027,
            lon = 129.07900236976,
            date = LocalDate.of(2025, 5, 12)
        )
    }

    // 날씨 데이터 상태 변화 감지
    LaunchedEffect(weather) {
        weather?.let {
            Log.d(TAG, "날씨 데이터 업데이트됨: $it")
        } ?: Log.d(TAG, "날씨 데이터가 null입니다")
    }

    val temperature = weather?.tempAvg?.toInt() ?: 0
    val feelsLike = weather?.tempFeelsLike?.toInt() ?: 0
    val rainAmount = weather?.rainAmount?.toInt() ?: 0
    val description = weather?.description ?: "맑음"

    Log.d(TAG, "UI 렌더링 - 기온: ${temperature}°C, 체감온도: ${feelsLike}°C, 강수량: ${rainAmount}mm, 날씨: $description")

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(360.dp)
            .height(184.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_location_pin),
                contentDescription = "위치 핀"
            )
            Text(
                text = "금정구 장전동",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight(600),
                    color = Color(0xFFA9ACB1),
                )
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .width(360.dp)
                .background(color = Color(0xFFF4F5F6), shape = RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            WeatherRow("기온", "$temperature", Color(0xFF25D061), "°C")

            WeatherRow("날씨", description, Color(0xFF25D061), "")

            WeatherRow("체감온도", "$feelsLike", Color(0xFF25D061), "°C")

            WeatherRow("강수량", "$rainAmount", Color(0xFF121417), "mm")
        }
    }
}

@Composable
fun WeatherRow(label: String, value: String, keyColor: Color, measure: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(252.dp)
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight(500),
                color = Color(0xFF64666A),
            )
        )
        Row() {
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                    fontWeight = FontWeight(400),
                    color = keyColor,
                )
            )
            if (measure.isNotEmpty()) {
                WidthSpacer(2.dp)
                Text(
                    text = measure,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF121417),
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun WeatherCardPreview() {
    WeatherCard()
}