package com.apptive.wotd.composable

import android.icu.text.ListFormatter.Width
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptive.wotd.R
import com.apptive.wotd.ui.theme.pretendard

enum class BottomTab(
    val label: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    Home("캘린더", R.drawable.btn_calendar_navbar_selected, R.drawable.btn_calendar_navbar),
    Chart("홈", R.drawable.btn_calendar_navbar_selected, R.drawable.btn_home_navbar),
    MyPage("마이페이지", R.drawable.btn_calendar_navbar_selected, R.drawable.btn_mypage_btnbar)
}

@Composable
fun BottomBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color(0xFFEBEEF4))
            .width(360.dp)
            .height(64.dp)
            .background(color = Color(0xFFFFFFFF))
            .navigationBarsPadding(),
    ) {
        BottomTab.values().forEach { tab ->
            BottomBarItem(
                tab = tab,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                mModifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BottomBarItem(
    tab: BottomTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    mModifier: Modifier = Modifier
) {
    val icon = if (isSelected) tab.selectedIcon else tab.unselectedIcon
    val textColor = if (isSelected) Color(0xFF25D061) else Color(0xFFC9CDD2)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = mModifier.noRippleClickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = tab.label,
            modifier = Modifier
                .size(24.dp)
                .padding(1.dp)
        )
        HeightSpacer(4.dp)
        Text(
            text = tab.label,
            fontSize = 10.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight(600),
            color = textColor,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun PreviewPage() {
    BottomBar(
        selectedTab = BottomTab.Home,
        onTabSelected = {}
    )
}