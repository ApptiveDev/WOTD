package com.apptive.wotd.model.recommend

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StylingViewModel @Inject constructor(
    private val repository: StylingRepository
) : ViewModel() {

    var isSubmitting by mutableStateOf(false)
    var isSubmitSuccess by mutableStateOf<Boolean?>(null)

    private val _stylingResult = MutableStateFlow<StylingSuggestionData?>(null)
    val stylingResult: StateFlow<StylingSuggestionData?> = _stylingResult

    private val _stylingItems = MutableStateFlow<List<StylingItem>>(emptyList())
    val stylingItems: StateFlow<List<StylingItem>> = _stylingItems

    private val _stylingError = MutableStateFlow<String?>(null)
    val stylingError: StateFlow<String?> = _stylingError

    fun submitItemList(token: String, name: String, deadline: LocalDate) {
        viewModelScope.launch {
            isSubmitting = true
            val result = repository.submitItemList(token, name, deadline.toString())
            isSubmitSuccess = result.isSuccess
            isSubmitting = false
        }
    }

    fun requestStyling(token: String, temp: Double, rain: Double) {
        viewModelScope.launch {
            isSubmitting = true
            _stylingResult.value = null
            _stylingItems.value = emptyList()
            _stylingError.value = null
            val result = repository.requestSuggestion(token, temp, rain)
            result
                .onSuccess { data ->
                    _stylingResult.value = data
                    _stylingItems.value = data.suggestions
                }
                .onFailure { e ->
                    _stylingError.value = e.message
                }
            isSubmitting = false
        }
    }

    private val _itemData = MutableStateFlow<ItemData?>(null)
    val itemData: StateFlow<ItemData?> = _itemData

    private val _itemError = MutableStateFlow<String?>(null)
    val itemError: StateFlow<String?> = _itemError


    suspend fun fetchItemListAndReturn(token: String, date: String): ItemData? {
        val result = repository.getItemList(token, date)
        return result.getOrNull()?.also {
            _itemData.value = it
        }
    }

    private val _itemById = MutableStateFlow<ItemData?>(null)
    val itemById: StateFlow<ItemData?> = _itemById

    fun requestItemById(token: String, itemId: Long) {
        viewModelScope.launch {
            val result = repository.getItemById(token, itemId)
            result
                .onSuccess { _itemById.value = it }
                .onFailure { e ->
                    _itemById.value = null
                    _stylingError.value = e.message ?: "물품 조회에 실패했어요."
                }
        }
    }

    suspend fun submitItemListAndReturn(token: String, name: String, deadline: LocalDate): Boolean {
        val result = repository.submitItemList(token, name, deadline.toString())
        return if (result.isSuccess) {
            isSubmitSuccess = true
            true
        } else {
            isSubmitSuccess = false
            false
        }
    }

}