package com.apptive.wotd.model.moodreport

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoodReportViewModel @Inject constructor(
    private val moodReportRepository: MoodReportRepository
) : ViewModel() {
    val addState = mutableStateOf<MoodReportResponse?>(null)
    val errorState = mutableStateOf<String?>(null)
    val allReportsState = mutableStateOf<List<MoodReportItem>?>(null)
    val singleReportState = mutableStateOf<MoodReportItem?>(null)

    fun addMoodReport(token: String, request: MoodReportRequestDTO) {
        viewModelScope.launch {
            val result = moodReportRepository.addMoodReport(token, request)
            if (result != null && result.isSuccess) {
                addState.value = result
            } else {
                errorState.value = result?.message ?: "네트워크 오류"
            }
        }
    }

    fun getAllMoodReports(token: String) {
        viewModelScope.launch {
            val result = moodReportRepository.getAllMoodReports(token)
            if (result != null && result.isSuccess) {
                allReportsState.value = result.data
            } else {
                errorState.value = result?.message ?: "네트워크 오류"
            }
        }
    }

    fun getMoodReport(token: String, moodReportId: Long) {
        viewModelScope.launch {
            val result = moodReportRepository.getMoodReport(token, moodReportId)
            singleReportState.value = result?.data
        }
    }

    fun updateMoodReport(token: String, request: MoodReportUpdateRequest) {
        viewModelScope.launch {
            val result = moodReportRepository.updateMoodReport(token, request)
            if (result != null && result.isSuccess) {
                addState.value = result
            } else {
                errorState.value = result?.message ?: "네트워크 오류"
            }
        }
    }

    fun deleteMoodReport(token: String, moodReportId: Long) {
        viewModelScope.launch {
            val result = moodReportRepository.deleteMoodReport(token, moodReportId)
            if (result != null && result.isSuccess) {
                addState.value = result
            } else {
                errorState.value = result?.message ?: "네트워크 오류"
            }
        }
    }

    fun clearState() {
        addState.value = null
        errorState.value = null
    }
} 