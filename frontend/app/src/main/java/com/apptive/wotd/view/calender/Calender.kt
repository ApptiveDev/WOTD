package com.apptive.wotd.view.calender

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.apptive.wotd.R
import com.apptive.wotd.composable.CameraBtn
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.composable.MoodReportBtn
import com.apptive.wotd.composable.OutfitGrid
import com.apptive.wotd.composable.calander.CalendarView
import com.apptive.wotd.composable.noRippleClickable
import com.apptive.wotd.ui.theme.pretendard
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage(
    navController: NavController
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val scrollState = rememberScrollState()
//    LaunchedEffect(Unit) {
//        navController.navigate("ProgressPage/1")
//    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        HeightSpacer(60.dp)
        CalendarView(
            selectedDate = selectedDate,
            onDateSelected = {
                selectedDate = it
            }
        )
        HeightSpacer(28.dp)
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
        HeightSpacer(10.dp)
        WeatherCard(viewModel = hiltViewModel())
        HeightSpacer(8.dp)
        CameraBtn( {} )
        HeightSpacer(8.dp)
        MoodReportBtn(navController)
        HeightSpacer(16.dp)
        TodayMoodCard()
        HeightSpacer(8.dp)
        OutfitGrid()

    }
}