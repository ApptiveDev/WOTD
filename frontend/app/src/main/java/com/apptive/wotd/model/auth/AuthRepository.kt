package com.apptive.wotd.model.auth

import android.util.Log
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: ServerAuthAPI
) {
    suspend fun sendSignUp(data: SignUpData): Pair<Boolean, SignUpTokenData?> {
        val request = SignUpRequest(
            agree = data.agree,
            access_token =data.access_token
        )

        return try {
            val response = authApi.signUp(request)
            if (response.isSuccessful && response.body()?.status == "success") {
                Pair(true, response.body()?.data)
            } else {
                Log.e("SignUp", "Response failed: ${response.errorBody()?.string()}")
                Pair(false, null)
            }
        } catch (e: Exception) {
            Log.e("SignUp", "Network error", e)
            Pair(false, null)
        }
    }
}