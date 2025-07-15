package com.apptive.wotd.model.auth

import com.apptive.wotd.model.weather.WeatherApi
import com.apptive.wotd.model.moodreport.MoodReportApi
import com.apptive.wotd.view.login.ApiResponse
import com.apptive.wotd.view.login.UserMeResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

interface ServerAuthAPI {
    @POST("kakao/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

    @GET("users/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<ApiResponse<UserMeResponse>>
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://43.203.233.18:8080/")
            .client(okHttpClient)
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

    @Provides
    @Singleton
    fun provideMoodReportApi(retrofit: Retrofit): MoodReportApi {
        return retrofit.create(MoodReportApi::class.java)
    }
}
