package com.apptive.wotd.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apptive.wotd.model.auth.SignUpViewModel
import com.apptive.wotd.view.calender.CalendarPage
import com.apptive.wotd.view.login.LoginPage

@Composable
fun NavGraph(startPage: String){
    val navController = rememberNavController()
    val signUpViewModel: SignUpViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = startPage
    ) {
        composable("LoginPage"){
            LoginPage(navController, signUpViewModel)
        }
        composable("CalenderPage"){
            CalendarPage()
        }
    }
}