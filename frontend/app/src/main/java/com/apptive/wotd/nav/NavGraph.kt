package com.apptive.wotd.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apptive.wotd.model.auth.SignUpViewModel
import com.apptive.wotd.view.main.MainPage
import com.apptive.wotd.view.calender.CalendarPage
import com.apptive.wotd.view.login.LoginPage
import com.apptive.wotd.view.main.LoadingPage
import com.apptive.wotd.view.main.ProgressPage

@Composable
fun NavGraph(startPage: String){
    val navController = rememberNavController()
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = startPage
    ) {
        composable("MainPage"){
            MainPage(navController)
        }
        composable("LoginPage"){
            LoginPage(navController, signUpViewModel)
        }
        composable("CalendarPage"){
            CalendarPage(navController)
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