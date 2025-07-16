package com.apptive.wotd.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.apptive.wotd.R
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.composable.WidthSpacer
import com.apptive.wotd.model.recommend.StylingSuggestionData
import com.apptive.wotd.ui.theme.pretendard
import coil.compose.AsyncImage

@Composable
fun TodaysOutfitRecommend(
    stylingResult: StylingSuggestionData? = null
) {
    var selected by remember { mutableStateOf(0) }
    var sliderPosition by remember { mutableStateOf(0.8f) }

    Column(
        horizontalAlignment = Alignment.Start,
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
                text = "오늘의 추천 코디",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF121417),
                )
            )
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.btn_info),
                contentDescription = "info 버튼",
            )
        }
        HeightSpacer(8.dp)
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFF4F5F6), shape = RoundedCornerShape(size = 8.dp))
                .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 24.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                stylingResult?.suggestions?.forEach { suggestion ->
                    item {
                        RecommendItem(top = suggestion.img_top, bottom = suggestion.img_bottom, etc = suggestion.img_etc)
                    }
                }
            }
            OutfitInfoRow(label = "최근에 이 옷을 입은 날짜", value1 = stylingResult?.suggestions?.firstOrNull()?.date ?: "알 수 없음")
            OutfitInfoRow(
                label = "해당 날짜의 기온 / 체감온도",
                value1 = stylingResult?.suggestions?.firstOrNull()?.temp_avg?.toInt()?.toString() ?: "-",
                value2 = stylingResult?.suggestions?.firstOrNull()?.temp_feels_like?.toInt()?.toString() ?: "-",
                keyColor = Color(0xFF25D061),
                measure = "°C"
            )
            Column(modifier = Modifier.fillMaxWidth()){
                OutfitInfoRow(label = "만족도", value1 = (sliderPosition*100).toInt().toString(), measure = "%")
                FrogSatisfactionSlider(sliderPosition, onValueChange = {sliderPosition = it})
            }

        }
    }
}

@Composable
fun RecommendItem(top: String, bottom: String, etc: String) {
    Row(
        modifier = Modifier
            .border(width = 1.dp, color = Color(0xFFEBEEF2), shape = RoundedCornerShape(4.dp))
            .width(180.dp)
            .height(60.dp)
            .background(color = Color(0xFFFCFCFC), shape = RoundedCornerShape(4.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = top,
            contentDescription = "상의 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        )
        AsyncImage(
            model = bottom,
            contentDescription = "하의 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        )
        AsyncImage(
            model = etc,
            contentDescription = "기타 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        )
    }
}

@Composable
fun OutfitInfoRow(
    label: String,
    value1: String,
    value2: String = "",
    keyColor: Color = Color(0xFF25D061),
    measure: String = ""
) {
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
                text = value1,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                    fontWeight = FontWeight(400),
                    color = if (value2.isNotEmpty()) keyColor else Color(0xFF121417),
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
            if (value2.isNotEmpty()) {
                Text(
                    text = "/",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF121417),
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = value2,
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrogSatisfactionSlider(
    sliderPosition: Float = 0.8f,
    onValueChange: (Float) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(sliderPosition) }
    Slider(
        value = sliderPosition,
        onValueChange = {
            sliderPosition = it
            onValueChange(it)
        },
        valueRange = 0f..1f,
        modifier = Modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent,
            activeTrackColor = Color(0xFF22C55E),
            inactiveTrackColor = Color(0xFF22C55E).copy(alpha = 0.2f)
        ),
        thumb = {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.btn_slider_frog),
                contentDescription = "frog"
            )
        }
    )
}