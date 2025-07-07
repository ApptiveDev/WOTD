package com.apptive.wotd.model.auth

data class SignUpData(
    val agree: Boolean = false,
    val accessToken: String = ""
)

data class SignUpRequest(
    val agree: Boolean,
    val accessToken: String
)

data class SignUpResponse(
    val isSuccess: Boolean,
    val message: String,
    val data: SignUpTokenData
)

data class SignUpTokenData(
    val id : Long,
    val name : String,
    val providerId : String,
    val providerType : String,
    val token: String
)
