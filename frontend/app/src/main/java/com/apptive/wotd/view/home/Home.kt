package com.apptive.wotd.view.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.apptive.wotd.composable.CameraBtn
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.ui.theme.pretendard
import com.apptive.wotd.view.calender.WeatherCard
import java.time.LocalDate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.apptive.wotd.model.auth.TokenManager
import com.apptive.wotd.model.weather.WeatherViewModel
import com.apptive.wotd.model.recommend.RecommendApi
import com.apptive.wotd.model.recommend.StylingSuggestionRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Composable
fun HomePage() {
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val weatherState = weatherViewModel.weather.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(weatherState) {
        val temp = weatherState?.tempFeelsLike
        val rain = weatherState?.rainAmount
        val token = TokenManager.getAccessToken(context) ?: ""
        val authHeader = "Bearer $token"
        if (temp != null && rain != null) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("http://43.203.233.18:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            val recommendApi = retrofit.create(RecommendApi::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = recommendApi.getStylingSuggestion(
                        token = authHeader,
                        request = StylingSuggestionRequest(
                            temp_feels_like = temp,
                            rain_amount = rain
                        )
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("HomePage", "stylingSuggestion 전체 응답: $body")
                        if (body?.isSuccess == true) {
                            Log.d("HomePage", "추천 메시지: ${body.data?.message}")
                            Log.d("HomePage", "추천 개수: ${body.data?.cnt_suggestions}")
                            Log.d("HomePage", "추천 리스트: ${body.data?.suggestions}")
                        } else {
                            Log.e("HomePage", "추천 실패: ${body?.message}, 에러코드: ${body?.errorCode}")
                        }
                    } else {
                        Log.e("HomePage", "StylingSuggestion 실패: code=${response.code()}, error=${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("HomePage", "StylingSuggestion 통신 오류", e)
                }
            }
        }
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 60.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "WOTD",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF121417)
                )
            )
            ChecklistBtn( {} )
        }
        HeightSpacer(12.dp)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .padding(1.dp)
                        .width(20.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_frog_satisfied_3),
                    contentDescription = "날씨 아이콘"
                )
                Text(
                    text = "오늘의 날씨",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF121417),
                    )
                )
            }
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
        }
        HeightSpacer(8.dp)
        WeatherCard(selectedDate = LocalDate.now(), viewModel = hiltViewModel())
        HeightSpacer(12.dp)
        CameraBtn( {} )
        HeightSpacer(12.dp)
        OutfitComment()
        HeightSpacer(12.dp)
        TodaysOutfitRecommend()
    }
}

@Composable
fun OutfitComment() {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(color = Color(0xFFE6FBED), shape = RoundedCornerShape(size = 8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "너무 짧은 의상은 오늘 날씨에 추울 수 있어요!\n얇은 가디건이나 셔츠는 어떨까요?",
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color(0xFF25D061),
            )
        )
    }
}

@Composable
fun ChecklistBtn(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(4.dp)
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_bag),
            contentDescription = "챙길 물품 리스트"
        )
        Text(
            text = "챙길 물품 리스트",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF121417)
            )
        )
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage()
}
