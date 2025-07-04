package com.apptive.wotd.composable

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptive.wotd.R
import com.apptive.wotd.ui.theme.pretendard

@Composable
fun MoodReportBtn() {
    val context = LocalContext.current

    OutlinedButton (
        onClick = {
        },
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF2BBF5F)
        ),
        border = BorderStroke(1.dp, Color(0xFF2BBF5F)),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = Modifier
            .width(320.dp)
            .height(40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.btn_mood_report),
                contentDescription = "무드 리포트 버튼"
            )
            Text(
                text = "무드 리포트 수정하기",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2BBF5F)
                )
            )
        }
    }
}

@Preview
@Composable
fun MoodReportBtnPreview() {
    MoodReportBtn()
}