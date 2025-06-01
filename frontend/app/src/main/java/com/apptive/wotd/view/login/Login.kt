package com.apptive.wotd.view.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.apptive.wotd.R
import com.apptive.wotd.composable.ButtonWithLogo
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.model.auth.KaKaoLoginViewModel
import com.apptive.wotd.model.auth.SignUpViewModel
import com.apptive.wotd.ui.theme.backgroundColor
import com.apptive.wotd.ui.theme.pretendard
import com.apptive.wotd.view.term.TermBottomSheet

@Composable
fun LoginPage(
    navController: NavController,
    signUpViewModel: SignUpViewModel,
    kakaoViewModel: KaKaoLoginViewModel = hiltViewModel(),
){
    val user by kakaoViewModel.user
    val context = LocalContext.current
    val token = kakaoViewModel.getAccessToken()
    var termsOfService by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        if (user != null && token != null) {
            signUpViewModel.updateToken(token)
            Log.d("accessToken: ", signUpViewModel.getToken())
            termsOfService = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        HeightSpacer(140.dp)
        Text(
            text = "WOTD",
            fontSize = 40.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight.W700
        )
        HeightSpacer(8.dp)
        Text(
            text = "Weather Outfit Of The Day",
            fontSize = 12.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight.W400
        )
        HeightSpacer(36.dp)
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.logo_wotd),
            contentDescription = "wotd frog logo",
            modifier = Modifier.size(249.dp, 253.dp)
        )
        Spacer(Modifier.weight(1f))
        ButtonWithLogo(
            backgroundColor = Color.White,
            textColor = Color.Black,
            textSize = 14,
            textWeight = 600,
            buttonText = "카카오 회원가입",
            logoResourceId = R.drawable.ic_kakao_login,
            onClick = {
                kakaoViewModel.kakaoLogin(context)
            }
        )
        HeightSpacer(8.dp)
        ButtonWithLogo(
            backgroundColor = Color.White,
            textColor = Color.Black,
            textSize = 14,
            textWeight = 600,
            buttonText = "Google 회원가입",
            logoResourceId = R.drawable.ic_google_login,
            onClick = {}
        )
        HeightSpacer(63.dp)
    }

    if (termsOfService) {
        TermBottomSheet(
            signUpViewModel,
            navController,
            onDismiss = { termsOfService = false }
        )
    }
}