package com.apptive.wotd.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apptive.wotd.model.auth.SignUpViewModel
import com.apptive.wotd.view.main.MainPage
import com.apptive.wotd.view.calender.CalendarPage
import com.apptive.wotd.view.home.HomePage
import com.apptive.wotd.view.login.LoginPage
import com.apptive.wotd.view.main.LoadingPage
import com.apptive.wotd.view.main.ProgressPage
import com.apptive.wotd.view.moodreport.MoodReportPage
import com.apptive.wotd.view.main.MainViewModel
import androidx.compose.ui.platform.LocalContext
import com.apptive.wotd.model.auth.LoginViewModel
import com.apptive.wotd.model.auth.TokenManager
import com.apptive.wotd.view.mypage.MyPage

@Composable
fun NavGraph(startPage: String){
    val navController = rememberNavController()
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val mainViewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val context = LocalContext.current
    val jwtToken = TokenManager.getAccessToken(context)
    LaunchedEffect(Unit) {
        if (jwtToken != null) {
            loginViewModel.loginWithJwt(
                token = jwtToken,
                onSuccess = {
                    navController.navigate("MainPage") {
                        popUpTo("LoginPage") { inclusive = true }
                    }
                },
                onFailure = { message ->
                    Log.e("Login", "실패: $message")
                }
            )
        }
    }
    NavHost(
        navController = navController,
        startDestination = startPage
    ) {
        composable("MainPage"){
            MainPage(navController, mainViewModel)
        }
        composable("LoginPage"){
            LoginPage(navController, signUpViewModel)
        }
        composable("CalendarPage"){
            CalendarPage(navController, mainViewModel)
        }
        composable("HomePage"){
            HomePage()
        }
        composable("MyPage"){
            MyPage()
        }
        composable("MoodReportPage/{moodReportId}") { backStackEntry ->
            val moodReportId = backStackEntry.arguments?.getString("moodReportId")?.toLongOrNull() ?: 0L
            val viewModel: com.apptive.wotd.model.moodreport.MoodReportViewModel = hiltViewModel()
            val singleReport = viewModel.singleReportState.value
            val origin = singleReport?.moodReport
            com.apptive.wotd.view.moodreport.MoodReportPage(
                moodReportId = moodReportId,
                origin = origin,
                mainViewModel = mainViewModel,
                navController = navController
            )
        }
        composable("ProgressPage/{phase}") { backStackEntry ->
            val phase = backStackEntry.arguments?.getString("phase") ?: ""
            ProgressPage(navController = navController, phase = phase.toInt())
        }
        composable("LoadingPage/{phase}") { backStackEntry ->
            val phase = backStackEntry.arguments?.getString("phase") ?: ""
            LoadingPage(navController = navController, phase = phase.toInt())
        }
    }
}