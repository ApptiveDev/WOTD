package com.apptive.wotd.view.main

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.apptive.wotd.R
import com.apptive.wotd.composable.BottomBar
import com.apptive.wotd.composable.BottomTab
import com.apptive.wotd.composable.CameraBtn
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.composable.WhiteScreenModifier
import com.apptive.wotd.ui.theme.backgroundColor
import com.apptive.wotd.ui.theme.primaryColor
import com.apptive.wotd.view.calender.CalendarPage
import kotlinx.coroutines.delay

@Composable
fun MainPage(
    navController: NavController
) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.Calendar) }

    Scaffold(
        bottomBar = {
            BottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = backgroundColor,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            when (selectedTab) {
                BottomTab.Home -> {}
                BottomTab.Calendar -> CalendarPage(navController)
                BottomTab.MyPage -> {}
            }
        }
    }
}

@Composable
fun ProgressPage(
    navController: NavController,
    phase: Int
) {
    val context = LocalContext.current
    Column(
        modifier = WhiteScreenModifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeightSpacer(84.dp)
        Text(
            text = "0$phase/03",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color(0xFF64666A),
                textAlign = TextAlign.Center,
            )
        )
        HeightSpacer(196.dp)
        Image(
            imageVector = ImageVector.vectorResource(
                if (phase == 1) {
                    R.drawable.ic_shirt
                } else if (phase == 2) {
                    R.drawable.ic_pants
                } else {
                    R.drawable.ic_etc
                }
            ),
            contentDescription = "의상",
        )
        HeightSpacer(48.dp)
        Text(
            text = if (phase == 1) {
                "상의를 촬영해주세요."
            } else if (phase == 2) {
                "하의를 촬영해주세요."
            } else {
                "기타 물품을 촬영해주세요."
            },
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color(0xFF121417),
                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        CameraBtn() {
            /*
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
             */
            navController.navigate(
                when (phase) {
                    1 -> "LoadingPage/1"
                    2 -> "LoadingPage/2"
                    else -> "LoadingPage/3"
                }
            )
        }
        HeightSpacer(58.dp)
    }
}

@Composable
fun LoadingPage(
    navController: NavController,
    phase: Int
) {
    LaunchedEffect(Unit) {
        delay(3000L)
        navController.navigate(
            when (phase) {
                1 -> "ProgressPage/2"
                2 -> "ProgressPage/3"
                else -> "CalendarPage"
            }
        )
    }

    Column(
        modifier = WhiteScreenModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
        Canvas(modifier = Modifier.size(size = 168.dp)) {
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
        HeightSpacer(24.dp)
        Text(
            text = "인식 중 입니다.",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color(0xFF121417)
            )
        )
        HeightSpacer(4.dp)
        Text(
            text = "잠시만 기다려주세요.",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color(0xFF121417)
            )
        )
    }
}