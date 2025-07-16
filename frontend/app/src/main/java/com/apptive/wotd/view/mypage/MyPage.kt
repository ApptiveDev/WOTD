package com.apptive.wotd.view.mypage

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptive.wotd.R
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.ui.theme.pretendard

@Composable
fun MyPage() {
    var marketingAgreed by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
            .padding(top = 60.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "계정",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight(700),
                    )
                )
            }

            HeightSpacer(24.dp)

            ClickableRoundedRow("계정 설정") { }
            HeightSpacer(16.dp)
            ClickableRoundedRow("이용약관") { }
            HeightSpacer(16.dp)
            ClickableRoundedRow("개인정보처리방침") { }
            HeightSpacer(16.dp)

            ToggleRow(
                label = "마케팅 수신 동의",
                checked = marketingAgreed,
                onToggle = { marketingAgreed = it }
            )
            HeightSpacer(16.dp)
            ToggleRow(
                label = "알림 설정",
                checked = notificationsEnabled,
                onToggle = { notificationsEnabled = it }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "로그아웃",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFB8BABD),
                )
            )

            Image(
                modifier = Modifier
                    .padding(1.dp)
                    .width(4.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_line_partition),
                contentDescription = "세로 선 파티션"
            )

            Text(
                text = "회원탈퇴",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFB8BABD),
                )
            )
        }
    }
}

@Composable
fun ClickableRoundedRow(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(
                onClick = onClick,
                indication = rememberRipple(bounded = true),
                interactionSource = interactionSource
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight(400),
            )
        )
    }
}

@Composable
fun ToggleRow(
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight(400),
            ),
            modifier = Modifier.clickable(
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onToggle(!checked)
            }
        )
        ToggleSwitch(
            checked = checked,
            onToggle = onToggle
        )
    }
}

@Composable
fun ToggleSwitch(
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val thumbColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF25D061) else Color(0xFFC6CAD1),
        label = "ThumbColor"
    )

    val thumbOffsetX by animateDpAsState(
        targetValue = if (checked) 8.dp else -8.dp,
        label = "ThumbOffset"
    )

    Box(
        modifier = Modifier
            .width(36.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFE3E5E6))
            .clickable { onToggle(!checked) }
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffsetX)
                .size(14.dp)
                .align(Alignment.Center)
                .background(thumbColor, shape = CircleShape)
        )
    }
}

@Preview
@Composable
fun MyPagePreview() {
    MyPage()
}