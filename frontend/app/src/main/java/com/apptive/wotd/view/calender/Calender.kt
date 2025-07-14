package com.apptive.wotd.view.calender

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import com.apptive.wotd.model.moodreport.MoodReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage(
    navController: NavController
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("image_links", Context.MODE_PRIVATE)
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    val scrollState = rememberScrollState()
    val moodReportViewModel: MoodReportViewModel = hiltViewModel()
    val allReports = moodReportViewModel.allReportsState.value
    val token = com.apptive.wotd.model.auth.TokenManager.getAccessToken(context)?.trim()

    LaunchedEffect(token) {
        if (!token.isNullOrBlank()) {
            moodReportViewModel.getAllMoodReports(token)
        }
    }
    LaunchedEffect(allReports) {
        if (allReports != null) {
            Log.d("CalendarPage", "전체 무드리포트 조회 결과: $allReports")
        }
    }
//    LaunchedEffect(Unit) {
//        navController.navigate("ProgressPage/1")
//    }
    val selectedDateString = selectedDate?.toString()
    val reportForSelectedDate = allReports?.find {
        it.moodReport?.date == selectedDateString
    }
    val hasReportForSelectedDate = reportForSelectedDate != null
    val isScoreFeelZero = reportForSelectedDate?.moodReport?.score_feel == 0.0
    val singleReport = moodReportViewModel.singleReportState.value
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
                Log.d("CalendarPage", "날짜 선택됨: $it")
                selectedDate = it
                prefs.edit().putString("selected_date", it.toString()).apply()
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
        WeatherCard(selectedDate = selectedDate, viewModel = hiltViewModel())
        HeightSpacer(8.dp)
        if (!hasReportForSelectedDate) {
            CameraBtn { navController.navigate("ProgressPage/1") }
            HeightSpacer(8.dp)
        } else if (isScoreFeelZero) {
            // 단건 무드리포트 조회 API 호출
            LaunchedEffect(reportForSelectedDate?.moodReport?.id) {
                val id = reportForSelectedDate?.moodReport?.id
                if (!token.isNullOrBlank() && id != null) {
                    moodReportViewModel.getMoodReport(token, id)
                }
            }
            LaunchedEffect(singleReport) {
                if (singleReport != null) {
                    Log.d("단건 무드리포트 조회", singleReport.toString())
                }
            }
            val moodReportData = singleReport?.data as? Map<*, *>
            val imgTop = moodReportData?.get("img_top") as? String
            val imgBottom = moodReportData?.get("img_bottom") as? String
            val imgEtc = moodReportData?.get("img_etc") as? String
            CameraBtn { navController.navigate("ProgressPage/1") }
            HeightSpacer(8.dp)
            MoodReportBtn(navController)
            HeightSpacer(8.dp)
            OutfitGrid(imgTop = imgTop, imgBottom = imgBottom, imgEtc = imgEtc)
        } else {
            MoodReportBtn(navController)
            HeightSpacer(16.dp)
            TodayMoodCard()
            HeightSpacer(8.dp)
            OutfitGrid()
        }
    }
}