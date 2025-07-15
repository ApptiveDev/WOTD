package com.apptive.wotd.view.moodreport
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.ui.theme.pretendard
import com.apptive.wotd.composable.OutfitGrid
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptive.wotd.model.moodreport.MoodReportViewModel
import com.apptive.wotd.model.moodreport.MoodReportUpdateRequest
import com.apptive.wotd.model.auth.TokenManager
import androidx.navigation.NavController
import android.widget.Toast
import com.apptive.wotd.view.main.MainViewModel
@Composable
fun MoodReportPage(
    moodReportId: Long,
    origin: com.apptive.wotd.model.moodreport.MoodReport?,
    mainViewModel: MainViewModel, // mainViewModel을 명확히 받음
    navController: NavController? = null
) {
    val context = LocalContext.current
    val token = TokenManager.getAccessToken(context)?.trim()
    val viewModel: MoodReportViewModel = hiltViewModel()
    val addState = viewModel.addState
    val singleReportState = viewModel.singleReportState.value
    var step by remember { mutableStateOf(1) }
    LaunchedEffect(origin, token, moodReportId) {
        if (origin == null && !token.isNullOrBlank() && moodReportId != 0L) {
            viewModel.getMoodReport(token, moodReportId)
        }
    }
    val actualOrigin = origin ?: singleReportState?.moodReport
    if (actualOrigin == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("무드리포트 정보를 불러오는 중입니다...")
        }
        return
    }
    var selected by remember { mutableStateOf(actualOrigin.score_feel?.toInt() ?: 0) } // 만족도
    var reviewText by remember { mutableStateOf(actualOrigin.content ?: "") } // 총평
    val reviewLength = reviewText.length
    val isAllFilled = selected != 0 && reviewText.isNotBlank() && reviewLength <= 500

    if (step == 2) {
        LaunchedEffect(Unit) {
            mainViewModel.setTab(com.apptive.wotd.composable.BottomTab.Calendar)
            navController?.navigate("MainPage") {
                popUpTo("MainPage") { inclusive = true }
                launchSingleTop = true
            }
        }
        return
    }

    LaunchedEffect(addState.value) {
        addState.value?.let { state ->
            if (state.isSuccess) {
                Toast.makeText(context, "무드리포트가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                viewModel.clearState()
                step = 2
            }
        }
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 60.dp)
    ) {
        OutfitGrid(
            imgTop = actualOrigin.img_top,
            imgBottom = actualOrigin.img_bottom,
            imgEtc = actualOrigin.img_etc
        )
        SatisfactionEdit(selected = selected, onSelect = { selected = it })
        HeightSpacer(25.dp)
        OverallReview(
            reviewText = reviewText,
            onReviewChange = {
                if (it.length <= 500) reviewText = it
            },
            reviewLength = reviewLength
        )
        HeightSpacer(25.dp)
        EditConfirmBtn(
            enabled = isAllFilled,
            onClick = {
                if (!token.isNullOrBlank() && actualOrigin != null) {
                    viewModel.updateMoodReport(
                        token,
                        MoodReportUpdateRequest(
                            id = actualOrigin.id,
                            date = actualOrigin.date,
                            created_at = actualOrigin.created_at,
                            latitude = actualOrigin.latitude,
                            longitude = actualOrigin.longitude,
                            img_top = actualOrigin.img_top,
                            img_bottom = actualOrigin.img_bottom,
                            img_etc = actualOrigin.img_etc,
                            content = reviewText,
                            score_feel = selected.toDouble()
                        )
                    )
                }
            }
        )
    }
}
@Composable
fun SatisfactionEdit(selected: Int, onSelect: (Int) -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "오늘의 코디 만족도는 어땠나요?",
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color.Black
            )
        )
        HeightSpacer(4.dp)
        Text(
            text = "ex) 날씨에 맞는 코디였나요??",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight(300),
                color = Color(0xFFA0A1A4)
            )
        )
        HeightSpacer(8.dp)
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (i in 1..5) {
                val iconRes = if (selected == i) {
                    when (i) {
                        1 -> R.drawable.ic_frog_satisfied_1
                        2 -> R.drawable.ic_frog_satisfied_2
                        3 -> R.drawable.ic_frog_satisfied_3
                        4 -> R.drawable.ic_frog_satisfied_4
                        5 -> R.drawable.ic_frog_satisfied_5
                        else -> R.drawable.ic_frog_satisfied_disabled_1
                    }
                } else {
                    when (i) {
                        1 -> R.drawable.ic_frog_satisfied_disabled_1
                        2 -> R.drawable.ic_frog_satisfied_disabled_2
                        3 -> R.drawable.ic_frog_satisfied_disabled_3
                        4 -> R.drawable.ic_frog_satisfied_disabled_4
                        5 -> R.drawable.ic_frog_satisfied_disabled_5
                        else -> R.drawable.ic_frog_satisfied_disabled_1
                    }
                }
                Image(
                    imageVector = ImageVector.vectorResource(iconRes),
                    contentDescription = "만족도 $i",
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onSelect(if (selected == i) 0 else i)
                        }
                )
            }
        }
    }
}
@Composable
fun OverallReview(reviewText: String, onReviewChange: (String) -> Unit, reviewLength: Int) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "오늘 코디에 대한 간단한 총평을 적어주세요.",
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color.Black
            )
        )
        HeightSpacer(8.dp)
        Box(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = reviewText,
                onValueChange = onReviewChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(159.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = pretendard,
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->
                    if (reviewText.isEmpty()) {
                        Text(
                            text = "어떤 내용이든 좋아요!",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = pretendard,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF93979D)
                            )
                        )
                    }
                    innerTextField()
                }
            )
            Text(
                text = "$reviewLength/500",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = pretendard,
                    color = if (reviewLength > 500) Color.Red else Color(0xFF93979D)
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
        }
    }
}
@Composable
fun EditConfirmBtn(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Color(0xFF2BBF5F) else Color(0xFFC9CDD2),
            contentColor = if (enabled) Color.White else Color(0xFF93979D)
        ),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.btn_mood_report_white),
                contentDescription = "무드리포트 작성",
                colorFilter = if (enabled) null else androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF93979D))
            )
            Text(
                text = "무드 리포트 작성 완료하기",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.White else Color(0xFF93979D)
                )
            )
        }
    }
}
@Preview
@Composable
fun MoodReportPagePreview() {
//    MoodReportPage(moodReportId = 1L, origin = null)
}