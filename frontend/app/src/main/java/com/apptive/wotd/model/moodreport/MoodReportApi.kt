package com.apptive.wotd.model.moodreport

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.Path

data class MoodReportRequestDTO(
    val date: String = "",
    val created_at: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val img_top: String = "",
    val img_bottom: String = "",
    val img_etc: String = "",
    val content: String = "",
    val score_feel: Double = 0.0,
)

data class MoodReportResponse(
    val isSuccess: Boolean,
    val message: String,
    val data: MoodReportItem? = null
)

data class MoodReportListResponse(
    val isSuccess: Boolean,
    val message: String,
    val data: List<MoodReportItem>?
)

data class MoodReportItem(
    val weatherData: WeatherData?,
    val moodReport: MoodReport?
)

data class WeatherData(
    val date: String?,
    val tempFeelsLike: Double?,
    val tempMin: Double?,
    val tempMax: Double?,
    val tempAvg: Double?,
    val rainAmount: Double?,
    val description: String?,
    val latitude: Double?,
    val longitude: Double?
)

data class MoodReport(
    val id: Long?,
    val userId: Long?,
    val weatherId: Long?,
    val date: String?,
    val created_at: String?,
    val latitude: Double?,
    val longitude: Double?,
    val img_top: String?,
    val img_bottom: String?,
    val img_etc: String?,
    val content: String?,
    val score_feel: Double?
)

data class MoodReportUpdateRequest(
    val id: Long?,
    val date: String?,
    val created_at: String?,
    val latitude: Double?,
    val longitude: Double?,
    val img_top: String?,
    val img_bottom: String?,
    val img_etc: String?,
    val content: String?,
    val score_feel: Double?
)

interface MoodReportApi {
    @POST("moodReport/add")
    suspend fun addMoodReport(
        @Header("Authorization") token: String,
        @Body request: MoodReportRequestDTO
    ): Response<MoodReportResponse>

    @GET("moodReport/requestAll")
    suspend fun getAllMoodReports(
        @Header("Authorization") token: String
    ): Response<MoodReportListResponse>

    @GET("moodReport/request/{moodReportId}")
    suspend fun getMoodReport(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("moodReportId") moodReportId: Long
    ): Response<MoodReportResponse>

    @POST("moodReport/update")
    suspend fun updateMoodReport(
        @Header("Authorization") token: String,
        @Body request: MoodReportUpdateRequest
    ): Response<MoodReportResponse>

    @DELETE("moodReport/delete/{moodReportId}")
    suspend fun deleteMoodReport(
        @Header("Authorization") token: String,
        @Path("moodReportId") moodReportId: Long
    ): Response<MoodReportResponse>
} 