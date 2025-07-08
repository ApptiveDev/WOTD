package com.apptive.wotd.view.calender

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.apptive.wotd.R
import com.apptive.wotd.ui.theme.pretendard

@Composable
fun TodayMoodCard() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF4F5F6), shape = RoundedCornerShape(size = 12.dp))
            .padding(start = 20.dp, top = 20.dp, end = 12.dp, bottom = 20.dp)
    ) {
        Text(
            text = "오늘의 만족도",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight(700),
                color = Color(0xFF64666A),
            )
        )
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            FrogMood(2)

            TextButton(
                onClick = { },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "무드리포트 보러가기 >",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontFamily = pretendard,
                        color = Color(0xFFA9ACB1),
                    )
                )
            }
        }
    }
}

@Composable
fun FrogMood(level: Int) {
    val (imageResId, percent) = when (level) {
        1 -> R.drawable.ic_frog_satisfied_1 to 20
        2 -> R.drawable.ic_frog_satisfied_2 to 40
        3 -> R.drawable.ic_frog_satisfied_3 to 60
        4 -> R.drawable.ic_frog_satisfied_4 to 80
        5 -> R.drawable.ic_frog_satisfied_5 to 100
        else -> R.drawable.ic_frog_satisfied_disabled_2 to 0
    }

    Row (
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = imageResId),
            contentDescription = "기분 $level"
        )
        Text(
            text = "${percent}%",
            style = TextStyle(
                fontSize = 17.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(500),
                color = Color.Black,
            )
        )
    }
}

@Preview
@Composable
fun TodayMoodCardPreview() {
    TodayMoodCard()
}

// 무드리포트 미작성시 안뜨게 해야함