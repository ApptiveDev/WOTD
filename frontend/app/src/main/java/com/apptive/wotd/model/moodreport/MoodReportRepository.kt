package com.apptive.wotd.model.moodreport

import android.util.Log
import javax.inject.Inject

class MoodReportRepository @Inject constructor(
    private val moodReportApi: MoodReportApi
) {
    suspend fun addMoodReport(token: String, request: MoodReportRequestDTO): MoodReportResponse? {
        return try {
            val response = moodReportApi.addMoodReport("Bearer $token", request)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MoodReportRepo", "무드리포트 추가 실패: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoodReportRepo", "무드리포트 추가 예외", e)
            null
        }
    }

    suspend fun getAllMoodReports(token: String): MoodReportListResponse? {
        return try {
            val response = moodReportApi.getAllMoodReports("Bearer $token")
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MoodReportRepo", "전체 무드리포트 조회 실패: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoodReportRepo", "전체 무드리포트 조회 예외", e)
            null
        }
    }

    suspend fun getMoodReport(token: String, moodReportId: Long): MoodReportResponse? {
        return try {
            val response = moodReportApi.getMoodReport("Bearer $token", moodReportId)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MoodReportRepo", "무드리포트 단건 조회 실패: "+response.errorBody()?.string())
                null
            }
        } catch (e: Exception) {
            Log.e("MoodReportRepo", "무드리포트 단건 조회 예외", e)
            null
        }
    }

    suspend fun updateMoodReport(token: String, request: MoodReportUpdateRequest): MoodReportResponse? {
        return try {
            val response = moodReportApi.updateMoodReport("Bearer $token", request)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MoodReportRepo", "무드리포트 수정 실패: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoodReportRepo", "무드리포트 수정 예외", e)
            null
        }
    }

    suspend fun deleteMoodReport(token: String, moodReportId: Long): MoodReportResponse? {
        return try {
            val response = moodReportApi.deleteMoodReport("Bearer $token", moodReportId)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MoodReportRepo", "무드리포트 삭제 실패: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MoodReportRepo", "무드리포트 삭제 예외", e)
            null
        }
    }
} 