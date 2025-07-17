package com.apptive.wotd.view.main
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.apptive.wotd.R
import com.apptive.wotd.composable.BottomBar
import com.apptive.wotd.composable.BottomTab
import com.apptive.wotd.composable.CameraBtn
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.composable.WhiteScreenModifier
import com.apptive.wotd.ui.theme.backgroundColor
import com.apptive.wotd.ui.theme.primaryColor
import com.apptive.wotd.view.calender.CalendarPage
import com.apptive.wotd.view.home.HomePage
import kotlinx.coroutines.delay
import androidx.compose.runtime.remember
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.apptive.wotd.model.moodreport.ImageApi
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context
import kotlinx.coroutines.withContext
import com.apptive.wotd.model.moodreport.MoodReportApi
import com.apptive.wotd.model.moodreport.MoodReportRequestDTO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.apptive.wotd.model.auth.TokenManager
import com.apptive.wotd.model.moodreport.MoodReportViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.collectAsState
import com.apptive.wotd.composable.HandleBackPressToExitApp
import com.apptive.wotd.view.mypage.MyPage

class MainViewModel : ViewModel() {
    private val _selectedTab = MutableStateFlow(BottomTab.Home)
    val selectedTab: StateFlow<BottomTab> = _selectedTab.asStateFlow()
    fun setTab(tab: BottomTab) { _selectedTab.value = tab }
}
@Composable
fun MainPage(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    val selectedTab by mainViewModel.selectedTab.collectAsState()
    Log.d("MainPage", "selectedTab: $selectedTab")
    HandleBackPressToExitApp(navController)
    Scaffold(
        bottomBar = {
            BottomBar(
                selectedTab = selectedTab,
                onTabSelected = { mainViewModel.setTab(it) }
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
                BottomTab.Home -> {
                    Log.d("MainPage", "HomePage 진입")
                    HomePage()
                }
                BottomTab.Calendar -> {
                    Log.d("MainPage", "CalendarPage 진입")
                    CalendarPage(navController, mainViewModel)
                }
                BottomTab.MyPage -> {
                    Log.d("MainPage", "MyPage 진입")
                    MyPage()
                }
            }
        }
    }
}
@Composable
fun ProgressPage(
    navController: NavController,
    phase: Int
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val moodReportViewModel: MoodReportViewModel = hiltViewModel()
    val addState by moodReportViewModel.addState
    val errorState by moodReportViewModel.errorState

    val prefs = context.getSharedPreferences("image_links", Context.MODE_PRIVATE)
    var imgTopLink by remember { mutableStateOf(prefs.getString("img_top", "") ?: "") }
    var imgBottomLink by remember { mutableStateOf(prefs.getString("img_bottom", "") ?: "") }
    var imgEtcLink by remember { mutableStateOf(prefs.getString("img_etc", "") ?: "") }
    val selectedDateString = prefs.getString("selected_date", LocalDate.now().toString()) ?: LocalDate.now().toString()

    fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "upload_image.png")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", file.name, requestFile)
    }
    val retrofit = Retrofit.Builder()
        .baseUrl("http://43.203.233.18:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val imageApi = retrofit.create(ImageApi::class.java)
    val moodReportApi = retrofit.create(MoodReportApi::class.java)

    fun uploadImage(
        newImagePart: MultipartBody.Part,
        onSuccess: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = imageApi.uploadImage(newImagePart)
            if (response.isSuccessful) {
                val newUrl = response.body()?.string() ?: ""
                withContext(Dispatchers.Main) { onSuccess(newUrl) }
            }
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uri = data?.data
            val goNext: () -> Unit = {
                navController.navigate(
                    when (phase) {
                        1 -> "ProgressPage/2"
                        2 -> "ProgressPage/3"
                        else -> {
                            val request = MoodReportRequestDTO(
                                date = selectedDateString,
                                created_at = LocalDate.now().toString(),
                                latitude = 35.2433,
                                longitude = 129.0752,
                                img_top = imgTopLink,
                                img_bottom = imgBottomLink,
                                img_etc = imgEtcLink,
                                content = " ",
                                score_feel = 0.0,
                            )
                            val rawToken = TokenManager.getAccessToken(context)
                            val token = rawToken?.trim()
                            if (!token.isNullOrBlank()) {
                                moodReportViewModel.addMoodReport(token, request)
                            } else {
                                Log.e("MoodReport", "토큰이 없습니다. 무드리포트 요청을 보내지 않습니다.")
                            }
                            "MainPage"
                        }
                    }
                )
            }
            if (uri != null) {
                imageUri = uri
                val part = uriToMultipart(context, uri)
                if (part != null) {
                    when (phase) {
                        1 -> uploadImage(part) { newUrl ->
                            imgTopLink = newUrl
                            prefs.edit().putString("img_top", imgTopLink).apply()
                            goNext()
                        }
                        2 -> uploadImage(part) { newUrl ->
                            imgBottomLink = newUrl
                            prefs.edit().putString("img_bottom", imgBottomLink).apply()
                            goNext()
                        }
                        3 -> uploadImage(part) { newUrl ->
                            imgEtcLink = newUrl
                            prefs.edit().putString("img_etc", imgEtcLink).apply()
                            goNext()
                        }
                    }
                }
            } else if (data?.extras?.get("data") != null) {
                val bitmap = data.extras?.get("data") as? android.graphics.Bitmap
                if (bitmap != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val file = File(context.cacheDir, "upload_image_camera.png")
                        val outputStream = FileOutputStream(file)
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.close()
                        val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("image", file.name, requestFile)
                        when (phase) {
                            1 -> uploadImage(part) { newUrl ->
                                imgTopLink = newUrl
                                prefs.edit().putString("img_top", imgTopLink).apply()
                                goNext()
                            }
                            2 -> uploadImage(part) { newUrl ->
                                imgBottomLink = newUrl
                                prefs.edit().putString("img_bottom", imgBottomLink).apply()
                                goNext()
                            }
                            3 -> uploadImage(part) { newUrl ->
                                imgEtcLink = newUrl
                                prefs.edit().putString("img_etc", imgEtcLink).apply()
                                goNext()
                            }
                        }
                    }
                } else {
                    goNext()
                }
            }
        }
    }
    fun showImagePicker() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val chooser = Intent.createChooser(galleryIntent, "이미지 선택 또는 촬영")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
        imageLauncher.launch(chooser)
    }
    Column(
        modifier = WhiteScreenModifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeightSpacer(84.dp)
        Text(
            text = "0$phase/03",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color(0xFF64666A),
                textAlign = TextAlign.Center,
            )
        )
        HeightSpacer(196.dp)
        Image(
            imageVector = ImageVector.vectorResource(
                if (phase == 1) {
                    R.drawable.ic_shirt
                } else if (phase == 2) {
                    R.drawable.ic_pants
                } else {
                    R.drawable.ic_etc
                }
            ),
            contentDescription = "의상",
        )
        HeightSpacer(48.dp)
        Text(
            text = if (phase == 1) {
                "상의를 촬영해주세요."
            } else if (phase == 2) {
                "하의를 촬영해주세요."
            } else {
                "기타 물품을 촬영해주세요."
            },
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color(0xFF121417),
                textAlign = TextAlign.Center,
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        CameraBtn() {
            showImagePicker()
        }
        HeightSpacer(58.dp)
    }

    // 결과 처리
    LaunchedEffect(addState) {
        if (addState != null) {
            prefs.edit().clear().apply()
            // 성공 후 페이지 이동 등 처리 가능
        }
    }
    LaunchedEffect(errorState) {
        if (errorState != null) {
            // 에러 처리 (예: Toast 등)
        }
    }
}

@Composable
fun LoadingPage(
    navController: NavController,
    phase: Int
) {
    LaunchedEffect(Unit) {
        delay(3000L)
        navController.navigate(
            when (phase) {
                1 -> "ProgressPage/2"
                2 -> "ProgressPage/3"
                else -> "MainPage"
            }
        )
    }
    Column(
        modifier = WhiteScreenModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val transition = rememberInfiniteTransition()
        val translateAnimation by transition.animateFloat(
            initialValue = 360f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1200,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )
        Canvas(modifier = Modifier.size(size = 168.dp)) {
            val startAngle = 5f
            val sweepAngle = 350f
            rotate(translateAnimation) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(0f)
                        ),
                        center = Offset(size.width / 2f, size.height / 2f)
                    ),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(6 / 2f, 6 / 2f),
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
                )
            }
        }
        HeightSpacer(24.dp)
        Text(
            text = "인식 중 입니다.",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color(0xFF121417)
            )
        )
        HeightSpacer(4.dp)
        Text(
            text = "잠시만 기다려주세요.",
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.ownglyph_corncorn)),
                fontWeight = FontWeight(400),
                color = Color(0xFF121417)
            )
        )
    }
}