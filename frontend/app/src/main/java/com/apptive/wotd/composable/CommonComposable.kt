package com.apptive.wotd.composable

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController

/**
 * 세로 간격을 띄우기 위한 Spacer입니다.
 * @param height (Dp)세로 간격을 지정합니다.
 * @author 김기윤
 */
@Composable
fun HeightSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

/**
 * 가로 간격을 띄우기 위한 Spacer입니다.
 * @param width (Dp)가로 간격을 지정합니다.
 * @author 김기윤
 */
@Composable
fun WidthSpacer(width: Dp) {
    Spacer(modifier = Modifier.width(width))
}

/**
 * 앱의 의도치 않은 종료를 막기 위한 Composable입니다.
 * @param navController
 * @author 김기윤
 */
@Composable
fun HandleBackPressToExitApp(
    navController: NavController
) {
    val context = LocalContext.current
    var lastBackPressTime by remember { mutableStateOf(0L) }

    BackHandler(enabled = true) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < 2000) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(context, "한 번 더 누르면 앱이 종료돼요", Toast.LENGTH_SHORT).show()
        }
    }
}