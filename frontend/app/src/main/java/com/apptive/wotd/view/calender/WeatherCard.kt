package com.apptive.wotd.view.calender

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptive.wotd.R
import com.apptive.wotd.ui.theme.pretendard
import java.time.LocalDate
import com.apptive.wotd.model.weather.WeatherViewModel
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.composable.WidthSpacer
import com.apptive.wotd.ui.theme.primaryColor

@Composable
fun WeatherCard(
    selectedDate: LocalDate? = null,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val keywords = listOf("thunderstorm", "drizzle", "rain", "snow", "clear", "clouds")

    val weather by viewModel.weather.collectAsState()
    val temperature = weather?.tempAvg?.toInt() ?: 0
    val feelsLike = weather?.tempFeelsLike?.toInt() ?: 0
    val rainAmount = weather?.rainAmount?.toInt() ?: 0
    val description = weather?.description
        ?.lowercase()
        ?.let { desc -> keywords.find { keyword -> keyword in desc } }
        ?: "clear"

    LaunchedEffect(key1 = selectedDate) {
        val dateToFetch = selectedDate ?: LocalDate.now()
        Log.d("WeatherCard", "날짜 변경됨: $dateToFetch")
        viewModel.fetchWeather(
            lat = 35.228700446027,
            lon = 129.07900236976,
            date = dateToFetch
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF4F5F6), shape = RoundedCornerShape(size = 12.dp))
            .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (weather) {
            null -> {
                val transition = rememberInfiniteTransition()
                val translateAnimation by transition.animateFloat(
                    initialValue = 360f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 1200,
                            easing = FastOutSlowInEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    )
                )
                Canvas(modifier = Modifier.size(size = 60.dp)) {
                    val startAngle = 5f
                    val sweepAngle = 350f

                    rotate(translateAnimation) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    primaryColor,
                                    primaryColor.copy(0f)
                                ),
                                center = Offset(size.width / 2f, size.height / 2f)
                            ),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(6 / 2f, 6 / 2f),
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
                        )
                    }
                }
            }

            else -> {
                WeatherRow("기온", "$temperature", Color(0xFF25D061), "°C")
                HeightSpacer(12.dp)
                DescriptionRow("$description")
                HeightSpacer(12.dp)
                WeatherRow("체감온도", "$feelsLike", Color(0xFF25D061), "°C")
                HeightSpacer(12.dp)
                WeatherRow("강수량", "$rainAmount", Color(0xFF121417), "mm")
            }
        }
    }
}

@Composable
fun DescriptionRow(value: String) {
    val (imgResId, txt) = when (value) {
        "thunderstorm" -> R.drawable.ic_weather_rainy to "비"
        "drizzle" -> R.drawable.ic_weather_rainy to "비"
        "rain" -> R.drawable.ic_weather_rainy to "비"
        "snow" -> R.drawable.ic_weather_snowy to "눈"
        "clear" -> R.drawable.ic_weather_sunny to "맑음"
        "clouds" -> R.drawable.ic_weather_cloudy to "흐림"
        else -> R.drawable.ic_weather_sunny to "맑음"
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "날씨",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight(500),
                color = Color(0xFF64666A),
            )
        )
        Row() {
            Image(
                painter = painterResource(imgResId),
                contentDescription = "날씨 아이콘 $value"
            )
            WidthSpacer(3.dp)
            Text(
                text = txt,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF64666A),
                )
            )
        }
    }
}


@Composable
fun WeatherRow(label: String, value: String, keyColor: Color, measure: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                    fontWeight = FontWeight(500),
                    color = if (value.isDigitsOnly()) keyColor else Color(0xFF121417),
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