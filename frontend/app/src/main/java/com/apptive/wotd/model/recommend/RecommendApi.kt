package com.apptive.wotd.model.recommend

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class StylingSuggestionRequest(
    val temp_feels_like: Double,
    val rain_amount: Double
)

data class StylingSuggestionItem(
    val moodReportId: Long,
    val weatherId: Long,
    val date: String,
    val img_top: String,
    val img_bottom: String,
    val img_etc: String,
    val score_feel: Double
)

data class StylingSuggestionData(
    val message: String,
    val cnt_suggestions: Int,
    val suggestions: List<StylingSuggestionItem>
)

data class StylingSuggestionResponse(
    val isSuccess: Boolean,
    val message: String,
    val data: StylingSuggestionData?,
    val errorCode: String? = null
)

interface RecommendApi {
    @POST("/stylingSuggestion/request")
    suspend fun getStylingSuggestion(
        @Header("Authorization") token: String,
        @Body request: StylingSuggestionRequest
    ): Response<StylingSuggestionResponse>
}
