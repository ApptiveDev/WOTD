package com.apptive.wotd.model.recommend

/* 챙길 물품 등록 */
data class PairingRequest(
    val name: String,
    val deadline: String
)

data class SimpleResponse(
    val isSuccess: Boolean
)

/* 추천 코디 받기 */
data class StylingRequest(
    val temp_feels_like: Double,
    val rain_amount: Double
)

data class StylingSuggestionResponse(
    val isSuccess: Boolean,
    val message: String,
    val data: StylingSuggestionData
)

data class StylingSuggestionData(
    val message: String,
    val cnt_suggestions: Int,
    val suggestions: List<StylingItem>
)

data class StylingItem(
    val moodReportId: Long,
    val weatherId: Long,
    val date: String,
    val temp_avg: Double,
    val temp_feels_like: Double,
    val img_top: String,
    val img_bottom: String,
    val img_etc: String,
    val score_feel: Double
)