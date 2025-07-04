package com.apptive.wotd.view.calender

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apptive.wotd.R
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.composable.calander.CalendarScreen
import com.apptive.wotd.composable.noRippleClickable
import com.apptive.wotd.ui.theme.pretendard
import com.google.common.io.Files.append
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptive.wotd.composable.BottomBar
import com.apptive.wotd.composable.BottomTab
import com.apptive.wotd.composable.CameraBtn
import com.apptive.wotd.composable.MoodReportBtn

@Composable
fun MonthlyCalendar(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    events: Set<LocalDate> = emptySet(),
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val days = remember(yearMonth) { generateCalendarDays(yearMonth) }

    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 29.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayOfWeek.entries.forEach {
                Text(
                    text = it.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 29.dp),
            userScrollEnabled = false
        ) {
            items(items = days) { date ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(10.dp)
                        .background(
                            color = when {
                                date == null -> Color.Transparent
                                date == selectedDate -> Color.Blue
                                date == today -> Color(0xFF25D061)
                                else -> Color.Transparent
                            }, shape = RoundedCornerShape(size = 32.dp)
                        )
                        .clickable(enabled = date != null) {
                            date?.let(onDateSelected)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = date?.dayOfMonth?.toString() ?: "",
                            color = when {
                                date == selectedDate -> Color.Gray
                                date == today -> Color.White
                                else -> Color.Black
                            },
                            fontFamily = pretendard,
                            fontSize = 14.sp,
                            fontWeight = FontWeight(400)
                        )
                        if (date != null && events.contains(date)) {
                            Spacer(Modifier.height(2.dp))
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .background(Color.Red, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage() {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val eventDates = remember {
        setOf(
            LocalDate.of(2025, 5, 10),
            LocalDate.of(2025, 5, 14)
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            MonthYearPicker(
                initialMonth = currentMonth,
                onSelect = {
                    currentMonth = it
                    coroutineScope.launch { sheetState.hide() }
                    showBottomSheet = false
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp) // BottomBar 공간 확보
                .pointerInput(Unit) {
                    var triggered = false
                    detectHorizontalDragGestures(
                        onDragEnd = { triggered = false }
                    ) { _, dragAmount ->
                        if (!triggered) {
                            when {
                                dragAmount > 30 -> {
                                    currentMonth = currentMonth.minusMonths(1)
                                    triggered = true
                                }

                                dragAmount < -30 -> {
                                    currentMonth = currentMonth.plusMonths(1)
                                    triggered = true
                                }
                            }
                        }
                    }
                }
        ) {
            HeightSpacer(60.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_calender_arrow_left),
                    contentDescription = "전월 이동",
                    Modifier.noRippleClickable { currentMonth = currentMonth.minusMonths(1) }
                )
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Black,
                                fontWeight = FontWeight(700),
                                fontFamily = pretendard,
                                fontSize = 20.sp
                            )
                        ) { append("${currentMonth.year}년 ") }

                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF25D061),
                                fontWeight = FontWeight(700),
                                fontFamily = pretendard,
                                fontSize = 20.sp
                            )
                        ) { append("${currentMonth.monthValue}") }

                        withStyle(
                            style = SpanStyle(
                                color = Color.Black,
                                fontWeight = FontWeight(700),
                                fontFamily = pretendard,
                                fontSize = 20.sp
                            )
                        ) { append("월") }
                    },
                    modifier = Modifier.noRippleClickable { showBottomSheet = true }
                )
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_calender_arrow_right),
                    contentDescription = "내월 이동",
                    Modifier.noRippleClickable { currentMonth = currentMonth.plusMonths(1) }
                )
            }

            HeightSpacer(24.dp)

            MonthlyCalendar(
                yearMonth = currentMonth,
                selectedDate = selectedDate,
                events = eventDates,
                onDateSelected = { selectedDate = it }
            )

            WeatherCard(viewModel = hiltViewModel())
            HeightSpacer(8.dp)
            CameraBtn()
            HeightSpacer(8.dp)
            MoodReportBtn()
            HeightSpacer(16.dp)
            TodayMoodCard()
            HeightSpacer(8.dp)
            OutfitGrid()
        }

        BottomBar(
            selectedTab = BottomTab.Calendar,
            onTabSelected = { /* TODO */ },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
fun CalenderScreenPreview() {
    CalendarPage()
}