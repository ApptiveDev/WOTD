package com.apptive.wotd.view.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptive.wotd.R
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.composable.WidthSpacer
import com.apptive.wotd.ui.theme.pretendard

@Composable
fun OutfitRecommend() {
//    val date = weather?.tempAvg?.toInt() ?: 0
//    val tempAvg = weather?.rainAmount?.toInt() ?: 0
//    val tempFeelsLike = weather?.description ?: "맑음"

    Column (
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
                text = "오늘의 날씨",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF121417),
                )
            )
            Image(
                modifier = Modifier
                    .padding(1.dp)
                    .width(16.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_info),
                contentDescription = "info 아이콘",
            )
        }
        HeightSpacer(8.dp)
        Column (
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFF4F5F6), shape = RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                RecommendItem()
                RecommendItem()
                RecommendItem()
                RecommendItem()
            }
        }
    }
}

@Composable
fun RecommendItem() {
    Row {
        Column(
            modifier = Modifier
                .border(width = 1.dp, color = Color(0xFFEBEEF2), shape = RoundedCornerShape(4.dp))
                .width(60.dp)
                .height(60.dp)
                .background(color = Color(0xFFFCFCFC), shape = RoundedCornerShape(4.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_frog_satisfied_4),
                contentDescription = "info 아이콘",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun OutfitInfoRow(label: String, value: String, keyColor: Color, measure: String) {
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
fun OutfitRecommendPreview() {
    OutfitRecommend()
}