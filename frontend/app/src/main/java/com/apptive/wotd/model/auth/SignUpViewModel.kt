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

    private val _token = mutableStateOf("")
    val token: State<String> = _token

    fun updateTermsOfServices(agreed: Boolean){
        _signUpData.value = _signUpData.value.copy(agree = agreed)
    }

    fun updateToken(token: String) {
        _signUpData.value = _signUpData.value.copy(accessToken = token)
    }

    fun getToken(): String = _signUpData.value.accessToken

    fun isAgreedToTerms(): Boolean = _signUpData.value.agree

    fun logSignUpData(tag: String = "SignUpData") {
        val data = _signUpData.value
        Log.d(tag, """
            - term: ${data.agree}
            - token: ${data.accessToken}
        """.trimIndent()
        )
    }

    fun completeSignUp(
        onSuccess: (token: String, name: String) -> Unit,
        onFailure: (Throwable?) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (result, tokenData) = authRepository.sendSignUp(_signUpData.value)
                if (result && tokenData != null) {
                    _token.value = tokenData.token
                    onSuccess(tokenData.token, tokenData.name)
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
