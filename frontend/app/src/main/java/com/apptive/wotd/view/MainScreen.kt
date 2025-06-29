package com.apptive.wotd.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.apptive.wotd.composable.BottomBar
import com.apptive.wotd.composable.BottomTab
import com.apptive.wotd.ui.theme.backgroundColor
import com.apptive.wotd.view.calender.CalendarPage

@Composable
fun MainScreen() {
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
                BottomTab.Calendar -> CalendarPage()
                BottomTab.MyPage -> {}
            }
        }
    }
}

@Composable
@Preview
fun MainScreenPreview() {
    MainScreen()
}