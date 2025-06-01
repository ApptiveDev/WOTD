package com.apptive.wotd.model.auth

data class SignUpData(
    val agree: Boolean = false,
    val access_token: String = ""
)

data class SignUpRequest(
    val agree: Boolean,
    val access_token: String
)

data class SignUpResponse(
    val status: String,
    val message: String,
    val data: SignUpTokenData
)

data class SignUpTokenData(
    val accessToken: String,
    val refreshToken: String
)
