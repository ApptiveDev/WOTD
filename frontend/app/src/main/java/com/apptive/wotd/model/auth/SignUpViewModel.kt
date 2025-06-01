package com.apptive.wotd.model.auth

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signUpData = mutableStateOf(SignUpData())
    val signUpData: State<SignUpData> = _signUpData

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _accessToken = mutableStateOf("")
    val accessToken: State<String> = _accessToken

    private val _refreshToken = mutableStateOf("")
    val refreshToken: State<String> = _refreshToken

    /*값 업데이트*/
    fun updateTermsOfServices(agreed: Boolean){
        _signUpData.value = _signUpData.value.copy(agree = agreed)
    }

    fun updateToken(token: String) {
        _signUpData.value = _signUpData.value.copy(access_token = token)
    }

    /*값 읽기*/
    // 토큰
    fun getToken(): String = _signUpData.value.access_token

    // 약관 동의 여부
    fun isAgreedToTerms(): Boolean = _signUpData.value.agree

    fun logSignUpData(tag: String = "SignUpData") {
        val data = _signUpData.value
        Log.d(tag, """
        - term: ${data.agree}
        - token: ${data.access_token}
    """.trimIndent())
    }

    fun completeSignUp(
        onSuccess: (accessToken: String, refreshToken: String) -> Unit,
        onFailure: (Throwable?) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (result, tokenData) = authRepository.sendSignUp(_signUpData.value)
                if (result && tokenData != null) {
                    _accessToken.value = tokenData.accessToken
                    _refreshToken.value = tokenData.refreshToken
                    onSuccess(tokenData.accessToken, tokenData.refreshToken)
                } else {
                    onFailure(null)
                }
            } catch (e: Exception) {
                onFailure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
