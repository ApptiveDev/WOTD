package com.apptive.wotd.composable

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
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
fun CameraBtn() {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(context.packageManager) != null) {
                if (context is Activity) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "카메라 실행 실패", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "카메라 앱을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        },
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2BBF5F),
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = Modifier
            .width(360.dp)
            .height(40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.btn_camera),
                contentDescription = "카메라 버튼"
            )
            Text(
                text = "오늘의 코디 촬영하기",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Preview
@Composable
fun CameraBtnPreview() {
    CameraBtn()
}