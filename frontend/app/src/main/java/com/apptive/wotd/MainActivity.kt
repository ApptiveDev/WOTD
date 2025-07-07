package com.apptive.wotd

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptive.wotd.model.auth.SignUpViewModel
import com.apptive.wotd.nav.NavGraph
import com.apptive.wotd.ui.theme.WOTDTheme
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WOTDTheme {
                Log.d("KeyHash", Utility.getKeyHash(this))
                val kakaoKey = BuildConfig.KAKAO_KEY
                Log.d("KakaoKey", kakaoKey)
                KakaoSdk.init(this, kakaoKey)
                KakaoMapSdk.init(this, kakaoKey)
                NavGraph("CalendarPage")
            }
        }
    }
}

