package com.apptive.wotd.view.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptive.wotd.R
import com.apptive.wotd.composable.CameraBtn
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.composable.WidthSpacer
import com.apptive.wotd.composable.noRippleClickable
import com.apptive.wotd.model.auth.TokenManager
import com.apptive.wotd.model.recommend.StylingViewModel
import com.apptive.wotd.model.weather.WeatherViewModel
import com.apptive.wotd.ui.theme.pretendard
import com.apptive.wotd.view.calender.WeatherCard
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetVisible by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var context = LocalContext.current
    val viewModel: StylingViewModel = hiltViewModel()
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    var token = TokenManager.getAccessToken(context).toString()
    val weather by weatherViewModel.weather.collectAsState()

    LaunchedEffect(Unit) {
        weatherViewModel.fetchWeather(
            lat = 35.228700446027,
            lon = 129.07900236976,
            date = LocalDate.now()
        )
    }

    LaunchedEffect(weather) {
        weather?.let {
            viewModel.requestStyling(
                token,
                it.tempAvg,
                it.rainAmount
            )
        }
    }
    val stylingResult by viewModel.stylingResult.collectAsState()
    Log.d("stylingResult", stylingResult?.message.toString())
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 60.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "WOTD",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF121417)
                )
            )
            ChecklistBtn({
                isSheetVisible = true
            })
        }
        HeightSpacer(12.dp)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
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
            }
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
        }
        HeightSpacer(8.dp)
        WeatherCard(selectedDate = LocalDate.now(), viewModel = weatherViewModel)
        HeightSpacer(12.dp)
        CameraBtn({})
        HeightSpacer(12.dp)
        OutfitComment(
            comment = when {
                stylingResult == null -> "로딩 중이에요..."
                stylingResult?.message.isNullOrBlank() -> "추천 결과가 없어요. 무드 리포트를 작성해볼까요?"
                else -> stylingResult!!.message
            }
        )
        HeightSpacer(12.dp)
        TodaysOutfitRecommend(stylingResult = stylingResult)
    }
    if (isSheetVisible) {
        val itemList = remember { mutableStateListOf<String>() }
        var deleteMenuIndex by remember { mutableStateOf<Int?>(null) }

        LaunchedEffect(Unit) {
            bottomSheetState.show()
        }
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    bottomSheetState.hide()
                }.invokeOnCompletion {
                    isSheetVisible = false
                }
            },
            sheetState = bottomSheetState,
            containerColor = Color.White,
            dragHandle = {
                Box(
                    Modifier
                        .padding(vertical = 13.5.dp)
                        .width(48.dp)
                        .height(5.dp)
                        .background(Color.Transparent, RoundedCornerShape(12.dp))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.Transparent)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_item_backpack),
                        contentDescription = "가방"
                    )
                    WidthSpacer(4.dp)
                    Text(
                        text = "챙길 물품 리스트",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = pretendard,
                            fontWeight = FontWeight(600),
                            color = Color(0xFF636363)
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.btn_main_exit),
                        contentDescription = "바텀시트 내리기",
                        modifier = Modifier.noRippleClickable {
                            scope.launch {
                                bottomSheetState.hide()
                            }.invokeOnCompletion {
                                isSheetVisible = false
                            }
                        }
                    )
                }
                HeightSpacer(24.dp)
                SmartHorizontalDatePicker(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
                HeightSpacer(32.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    itemList.forEachIndexed { index, item ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isFocused by interactionSource.collectIsFocusedAsState()
                        Column {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .fillMaxWidth()
                                    .height(32.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFC6CAD1), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                BasicTextField(
                                    value = item,
                                    onValueChange = { itemList[index] = it },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 12.sp,
                                        fontFamily = pretendard,
                                        fontWeight = FontWeight(600),
                                        color = Color(0xFF64666A),
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp),
                                    interactionSource = interactionSource,
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (!isFocused && item.isBlank()) {
                                                Text(
                                                    text = "물품을 입력해주세요",
                                                    fontSize = 12.sp,
                                                    fontFamily = pretendard,
                                                    fontWeight = FontWeight.Normal,
                                                    color = Color(0xFFB0B0B0),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                            if (item.isNotBlank()) {
                                                Image(
                                                    imageVector = ImageVector.vectorResource(R.drawable.btn_erase),
                                                    contentDescription = "삭제",
                                                    modifier = Modifier
                                                        .align(Alignment.CenterEnd)
                                                        .noRippleClickable {
                                                            itemList.removeAt(index)
                                                            deleteMenuIndex = null
                                                        }
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                            HeightSpacer(8.dp)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF25D061),
                            shape = RoundedCornerShape(size = 8.dp)
                        )
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFCFCFC),
                            shape = RoundedCornerShape(size = 8.dp)
                        )
                        .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
                        .noRippleClickable {
                            val itemStringToSend = itemList.joinToString(",")
                            viewModel.submitItemList(
                                token,
                                itemStringToSend,
                                selectedDate
                            )
                            scope.launch {
                                bottomSheetState.hide()
                            }.invokeOnCompletion {
                                isSheetVisible = false
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "등록하기",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = pretendard,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF25D061),
                        )
                    )
                }
                if (itemList.size < 20) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFF25D061),
                                shape = RoundedCornerShape(size = 8.dp)
                            )
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFFFCFCFC),
                                shape = RoundedCornerShape(size = 8.dp)
                            )
                            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 8.dp)
                            .noRippleClickable {
                                itemList.add("")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.btn_item_plus),
                                contentDescription = ""
                            )
                            WidthSpacer(4.dp)
                            Text(
                                text = "버튼을 눌러 챙길물품을 추가해주세요.",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontFamily = pretendard,
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF25D061),
                                )
                            )
                        }
                    }
                }
                HeightSpacer(40.dp)
            }
        }
    }
}

@Composable
fun OutfitComment(
    comment:String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(color = Color(0xFFE6FBED), shape = RoundedCornerShape(size = 8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = comment,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color(0xFF25D061),
            )
        )
    }
}

@Composable
fun ChecklistBtn(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(4.dp)
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_bag),
            contentDescription = "챙길 물품 리스트"
        )
        Text(
            text = "챙길 물품 리스트",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF121417)
            )
        )
    }
}

@Composable
fun SmartHorizontalDatePicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val totalDays = 10000
    val centerIndex = totalDays / 2
    val dates = remember {
        List(totalDays) { i -> LocalDate.now().minusDays((centerIndex - i).toLong()) }
    }

    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp

    val dateItemWidth = 28.dp

    LaunchedEffect(Unit) {
        val offsetPx = with(density) {
            (screenWidthDp.toPx() / 2f - dateItemWidth.toPx() / 2f).toInt()
        }
        listState.scrollToItem(centerIndex, -offsetPx)
    }

    val centerDate = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            val center = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
            val closest = visibleItems.minByOrNull { item ->
                kotlin.math.abs((item.offset + item.size / 2) - center)
            }
            closest?.index?.let { dates[it] } ?: selectedDate
        }
    }

    val currentMonth = YearMonth.from(centerDate.value)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )

        HeightSpacer(8.dp)

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(dates) { _, date ->
                val isSelected = date == selectedDate
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color(0xFF22C55E)
                            else Color.Transparent
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage()
}