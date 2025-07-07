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
import javax.inject.Singleton

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
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://43.203.233.18:8080/") // 서버 주소
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideServerAuthAPI(retrofit: Retrofit): ServerAuthAPI {
        return retrofit.create(ServerAuthAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }
}
