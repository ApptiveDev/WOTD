package com.apptive.wotd.model.auth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ServerAuthAPI {
    @POST("auth/kakao/login")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

//    @POST("terms")
//    suspend fun submitTerms(
//        @Header("Authorization") token: String,
//        @Body request: TermsRequest
//    ): Response<Unit>
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://43.203.255.97:8080/") // BaseUrl
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideServerAuthAPI(retrofit: Retrofit): ServerAuthAPI {
        return retrofit.create(ServerAuthAPI::class.java)
    }
}
