package com.apptive.wotd.view.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.apptive.wotd.model.auth.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.apptive.wotd.model.auth.LoginViewModel
import com.apptive.wotd.model.auth.LoginResult

data class UserMeResponse(
    val id: Long,
    val providerId: String,
    val providerType: String,
    val name: String,
    val latitude: Double?,
    val longitude: Double?,
    val allow_notification: Boolean,
    val createdAt: String
)

data class ApiResponse<T>(
    val isSuccess: Boolean,
    val message: String,
    val data: T,
    val errorCode: String?
)

interface UserApi {
    @GET("users/me")
    suspend fun getMe(@Header("Authorization") token: String): retrofit2.Response<ApiResponse<UserMeResponse>>
}

@Composable
fun LoginPage(
    navController: NavController,
    signUpViewModel: SignUpViewModel,
    kakaoViewModel: KaKaoLoginViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
){
    val user by kakaoViewModel.user
    val context = LocalContext.current
    val token = kakaoViewModel.getAccessToken()
    var termsOfService by remember { mutableStateOf(false) }
    var isLoginMode by remember { mutableStateOf(false) }
    val loginState = loginViewModel.loginState.value

    LaunchedEffect(loginState) {
        if (loginState is LoginResult.Success) {
            navController.navigate("MainPage") {
                popUpTo(0)
            }
        }
    }

    LaunchedEffect(user) {
        if (user != null && token != null && !isLoginMode) {
            signUpViewModel.updateToken(token)
            Log.d("accessToken: ", signUpViewModel.getToken())
            termsOfService = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(horizontal = 30.dp)
            .navigationBarsPadding(),
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
            buttonText = if (isLoginMode) "카카오 로그인" else "카카오 회원가입",
            logoResourceId = R.drawable.ic_kakao_login,
            onClick = {
                if (isLoginMode) {
                    val token = TokenManager.getAccessToken(context) ?: ""
                    loginViewModel.loginWithJwt(token)
                } else {
                    kakaoViewModel.kakaoLogin(context) { kakaoAccessToken ->
                        signUpViewModel.updateToken(kakaoAccessToken)
                        signUpViewModel.completeSignUp(
                            onSuccess = { jwt, name ->
                                TokenManager.saveData(context, jwt, name)
                                Log.d("JWT 저장", "token: $jwt")
                                // navController.navigate("main") 등으로 이동 처리 필요
                            },
                            onFailure = {
                                Log.e("SignUp", "회원가입/로그인 실패")
                            }
                        )
                    }
                }
            }
        )
        HeightSpacer(8.dp)
        ButtonWithLogo(
            backgroundColor = Color.White,
            textColor = Color.Black,
            textSize = 14,
            textWeight = 600,
            buttonText = if (isLoginMode) "Google 로그인" else "Google 회원가입",
            logoResourceId = R.drawable.ic_google_login,
            onClick = {}
        )
        TextButton(
            onClick = {
                if (!isLoginMode) {
                    isLoginMode = true
                }
            },
            enabled = !isLoginMode
        ) {
            Text(
                text = "이미 계정이 있으신가요?",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight(400),
                    color = if (isLoginMode) Color.White else Color(0xFF121417),
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }

    if (termsOfService) {
        TermBottomSheet(
            signUpViewModel,
            navController,
            onDismiss = { termsOfService = false }
        )
    }
}