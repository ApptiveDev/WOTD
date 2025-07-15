package com.apptive.wotd.model.auth

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptive.wotd.view.login.UserMeResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val loginState = mutableStateOf<LoginResult?>(null)
    val userMeState = mutableStateOf<UserMeResponse?>(null)
    val errorState = mutableStateOf<String?>(null)

    fun loginWithJwt(
        token: String,
        onSuccess: () -> Unit = {},
        onFailure: (String?) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val result = authRepository.getMe(token)
                if (result.isSuccess) {
                    userMeState.value = result.data
                    loginState.value = LoginResult.Success
                    onSuccess()
                } else {
                    errorState.value = result.message
                    loginState.value = LoginResult.Failure
                    onFailure(result.message)
                }
            } catch (e: Exception) {
                errorState.value = e.message
                loginState.value = LoginResult.Failure
                onFailure(e.message)
            }
        }
    }

    fun clearError() {
        errorState.value = null
    }
}

sealed class LoginResult {
    object Success : LoginResult()
    object Failure : LoginResult()
} 