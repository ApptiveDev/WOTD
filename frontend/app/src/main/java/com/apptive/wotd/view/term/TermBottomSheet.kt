package com.apptive.wotd.view.term

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.apptive.wotd.R
import com.apptive.wotd.composable.HeightSpacer
import com.apptive.wotd.composable.NextButton
import com.apptive.wotd.composable.WidthSpacer
import com.apptive.wotd.composable.noRippleClickable
import com.apptive.wotd.model.auth.SignUpViewModel
import com.apptive.wotd.model.auth.TokenManager
import com.apptive.wotd.ui.theme.pretendard
import com.apptive.wotd.ui.theme.primaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermBottomSheet(
    vm: SignUpViewModel = hiltViewModel(),
    navController: NavController,
    onDismiss: () -> Unit
) {
    var isEssentialChecked by remember { mutableStateOf(false) }
    var isOptionalChecked by remember { mutableStateOf(false) }
    var isEssentialExpanded by remember { mutableStateOf(false) }
    var isOptionalExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current


    val essentialRotation by animateFloatAsState(
        targetValue = if (isEssentialExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "essential_arrow_rotation"
    )

    val optionalRotation by animateFloatAsState(
        targetValue = if (isOptionalExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "optional_arrow_rotation"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        dragHandle = {
            Box(
                Modifier
                    .padding(top = 8.dp, bottom = 11.dp)
                    .width(48.dp)
                    .height(5.dp)
                    .background(
                        color = Color(0xFFE2E4EC),
                        shape = RoundedCornerShape(size = 12.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            HeightSpacer(16.dp)
            Text(
                text = "본인인증을 위해 동의가 필요해요",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF323439),
                ),
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp)
            )
            HeightSpacer(12.dp)
            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(imageVector =
                if (!isEssentialChecked)
                    ImageVector.vectorResource(R.drawable.btn_gray_checkmark)
                else
                    ImageVector.vectorResource(R.drawable.btn_blue_checkmark),
                    contentDescription = "checkBtn",
                    modifier = Modifier
                        .size(20.dp, 20.dp)
                        .noRippleClickable { isEssentialChecked = !isEssentialChecked }
                )
                WidthSpacer(8.dp)
                Text(
                    text = "[필수] 서비스 이용약관",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF686D78),
                    ),
                    modifier = Modifier.noRippleClickable {
                        isEssentialChecked = !isEssentialChecked
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.btn_announce_arrow),
                    contentDescription = "description",
                    modifier = Modifier
                        .noRippleClickable {
                            isEssentialExpanded = !isEssentialExpanded
                        }
                        .graphicsLayer(rotationZ = essentialRotation)
                )
            }
            AnimatedVisibility(visible = isEssentialExpanded) {
                EssentialTerms()
            }
            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector =
                    if (!isOptionalChecked)
                        ImageVector.vectorResource(R.drawable.btn_gray_checkmark)
                    else
                        ImageVector.vectorResource(R.drawable.btn_blue_checkmark),
                    contentDescription = "checkBtn",
                    modifier = Modifier
                        .size(20.dp, 20.dp)
                        .noRippleClickable { isOptionalChecked = !isOptionalChecked }
                )
                WidthSpacer(8.dp)
                Text(
                    text = "[선택] 마케팅 수신동의",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF686D78),
                    ),
                    modifier = Modifier.noRippleClickable { isOptionalChecked = !isOptionalChecked }
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.btn_announce_arrow),
                    contentDescription = "grayCheck",
                    modifier = Modifier
                        .noRippleClickable {
                            isOptionalExpanded = !isOptionalExpanded
                        }
                        .graphicsLayer(rotationZ = optionalRotation)
                )
            }
            AnimatedVisibility(visible = isOptionalExpanded) {
                OptionalTerms()
            }
            HeightSpacer(16.dp)
            NextButton(
                mModifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(58.dp),
                text = "확인",
                buttonColor = if (isEssentialChecked) primaryColor else Color(0xFFCADCF5),
                onClick = {
                    if(isEssentialChecked){
                        vm.updateTermsOfServices(true)
                        vm.logSignUpData()
                        onDismiss()
                        vm.completeSignUp(
                            onSuccess = { token, name ->
                                TokenManager.saveData(context, token, name)
                                navController.navigate("CalendarPage") {
                                    popUpTo(0)
                                }
                            },
                            onFailure = { error ->
                                Toast.makeText(
                                    context,
                                    error?.message ?: "회원가입에 실패했습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            )
        }
    }
}