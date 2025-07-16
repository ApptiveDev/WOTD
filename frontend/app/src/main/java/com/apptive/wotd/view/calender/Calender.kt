package com.apptive.wotd.view.calender
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.apptive.wotd.R
import com.apptive.wotd.composable.CameraBtn
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.composable.MoodReportBtn
import com.apptive.wotd.composable.OutfitGrid
import com.apptive.wotd.composable.calander.CalendarView
import com.apptive.wotd.model.moodreport.MoodReportViewModel
import com.apptive.wotd.ui.theme.pretendard
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage(
    navController: NavController,
    mainViewModel: com.apptive.wotd.view.main.MainViewModel
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("image_links", Context.MODE_PRIVATE)
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var selectedMoodReportId by remember { mutableStateOf<Long?>(null) }
    val scrollState = rememberScrollState()
    val moodReportViewModel: MoodReportViewModel = hiltViewModel()
    val allReports = moodReportViewModel.allReportsState.value
    val token = com.apptive.wotd.model.auth.TokenManager.getAccessToken(context)?.trim()
    val selectedTab by mainViewModel.selectedTab.collectAsState()
    LaunchedEffect(selectedTab) {
        if (selectedTab == com.apptive.wotd.composable.BottomTab.Calendar && !token.isNullOrBlank()) {
            moodReportViewModel.getAllMoodReports(token)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, token, selectedDate) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && !token.isNullOrBlank()) {
                moodReportViewModel.getAllMoodReports(token)
                val report = allReports?.find { it.moodReport?.date == selectedDate?.toString() }
                val id = report?.moodReport?.id
                if (id != null) {
                    moodReportViewModel.getMoodReport(token, id)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
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
    LaunchedEffect(allReports, selectedDate) {
        val report = allReports?.find { it.moodReport?.date == selectedDate?.toString() }
        selectedMoodReportId = report?.moodReport?.id
    }
    LaunchedEffect(selectedMoodReportId, token) {
        if (selectedMoodReportId != null && !token.isNullOrBlank()) {
            Log.d("SingleReportDebug", "단일조회 호출: id=$selectedMoodReportId")
            moodReportViewModel.getMoodReport(token, selectedMoodReportId!!)
        }
    }
    LaunchedEffect(moodReportViewModel.singleReportState.value) {
        if (moodReportViewModel.singleReportState.value != null) {
            Log.d("SingleReportDebug", "단일조회 결과: ${moodReportViewModel.singleReportState.value?.moodReport}")
        }
    }
    val addState = moodReportViewModel.addState
    LaunchedEffect(addState.value) {
        addState.value?.let { state ->
            if (state.isSuccess) {
                Toast.makeText(context, "무드리포트가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                moodReportViewModel.clearState()
                if (!token.isNullOrBlank()) {
                    moodReportViewModel.getAllMoodReports(token)
                }
            }
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
    var imgTop by remember { mutableStateOf<String?>(null) }
    var imgBottom by remember { mutableStateOf<String?>(null) }
    var imgEtc by remember { mutableStateOf<String?>(null) }
    if (isScoreFeelZero) {
        imgTop = singleReport?.moodReport?.img_top
        imgBottom = singleReport?.moodReport?.img_bottom
        imgEtc = singleReport?.moodReport?.img_etc
    } else {
        imgTop = reportForSelectedDate?.moodReport?.img_top
        imgBottom = reportForSelectedDate?.moodReport?.img_bottom
        imgEtc = reportForSelectedDate?.moodReport?.img_etc
    }
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
            },
            onDateLongPressed = { date ->
                val report = allReports?.find { it.moodReport?.date == date.toString() }
                if (report?.moodReport?.id != null && !token.isNullOrBlank()) {
                    moodReportViewModel.deleteMoodReport(token, report.moodReport.id!!)
                } else {
                    Toast.makeText(context, "해당 날짜에 무드리포트가 없습니다.", Toast.LENGTH_SHORT).show()
                }
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
            CameraBtn { navController.navigate("ProgressPage/1") }
            HeightSpacer(8.dp)
            MoodReportBtn(navController, selectedMoodReportId)
            HeightSpacer(8.dp)
            LaunchedEffect(singleReport) {
                if (singleReport?.moodReport != null) {
                    imgTop = singleReport.moodReport.img_top
                    imgBottom = singleReport.moodReport.img_bottom
                    imgEtc = singleReport.moodReport.img_etc
                }
            }
            if (singleReport?.moodReport != null) {
                Log.d("OutfitGridDebug", "imgTop=$imgTop, imgBottom=$imgBottom, imgEtc=$imgEtc")
                OutfitGrid(imgTop = imgTop, imgBottom = imgBottom, imgEtc = imgEtc)
            } else {
                Text("이미지 정보를 불러오는 중입니다...")
            }
        } else {
            CameraBtn { navController.navigate("ProgressPage/1") }
            HeightSpacer(8.dp)
            MoodReportBtn(navController, selectedMoodReportId)
            HeightSpacer(16.dp)
            val scoreFeel = reportForSelectedDate?.moodReport?.score_feel
            TodayMoodCard(scoreFeel = scoreFeel)
            HeightSpacer(8.dp)
            val imgTop = reportForSelectedDate?.moodReport?.img_top
            val imgBottom = reportForSelectedDate?.moodReport?.img_bottom
            val imgEtc = reportForSelectedDate?.moodReport?.img_etc
            Log.d("OutfitGridDebug", "imgTop=$imgTop, imgBottom=$imgBottom, imgEtc=$imgEtc")
            OutfitGrid(imgTop = imgTop, imgBottom = imgBottom, imgEtc = imgEtc)
        }
    }
}