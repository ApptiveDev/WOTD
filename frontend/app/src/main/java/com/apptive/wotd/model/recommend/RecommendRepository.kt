package com.apptive.wotd.model.recommend

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject

interface StylingApi {
    @POST("/item/add")
    suspend fun submitPairingItem(
        @Header("Authorization") token: String,
        @Body request: PairingRequest
    ): Response<SimpleResponse>

    @POST("/stylingSuggestion/request")
    suspend fun requestStyling(
        @Header("Authorization") token: String,
        @Body request: StylingRequest
    ): Response<StylingSuggestionResponse>
}


class StylingRepository @Inject constructor(
    private val api: StylingApi
) {
    suspend fun submitItemList(token: String, name: String, deadline: String): Result<Unit> {
        return try {
            val request = PairingRequest(name, deadline)
            val response = api.submitPairingItem("Bearer $token", request)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("등록 실패"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestSuggestion(token: String, temp: Double, rain: Double): Result<StylingSuggestionData> {
        return try {
            val response = api.requestStyling("Bearer $token", StylingRequest(temp, rain))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.isSuccess == true && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("추천 없음 또는 실패: ${body?.message}"))
                }
            } else {
                Result.failure(Exception("HTTP 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

