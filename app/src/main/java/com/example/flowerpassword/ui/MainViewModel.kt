package com.example.flowerpassword.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.flowerpassword.data.AppDatabase
import com.example.flowerpassword.data.HistoryItem
import com.example.flowerpassword.data.PreferencesManager
import com.example.flowerpassword.logic.PasswordGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MainUiState(
    val keyword: String = "",
    val code: String = "",
    val generatedPassword: String = "",
    val showPassword: Boolean = false,
    val rememberKeyword: Boolean = true,  // Always remember by default
    val historyItems: List<HistoryItem> = emptyList(),
    val errorMessage: String? = null
)

class MainViewModel(
    private val database: AppDatabase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    // Debounce job for saving history
    private var historySaveJob: Job? = null
    private val HISTORY_SAVE_DELAY_MS = 5000L  // 5 seconds

    init {
        // Load history
        viewModelScope.launch {
            database.historyDao().getAll().collect { items ->
                _uiState.update { it.copy(historyItems = items) }
            }
        }

        // Load saved keyword (always remember now)
        viewModelScope.launch {
            preferencesManager.savedKeyword.collect { saved ->
                if (saved.isNotEmpty() && _uiState.value.keyword.isEmpty()) {
                    _uiState.update { it.copy(keyword = saved) }
                }
            }
        }
    }

    fun updateKeyword(keyword: String) {
        _uiState.update { it.copy(keyword = keyword, errorMessage = null) }
        autoGeneratePassword()
    }

    fun updateCode(code: String) {
        _uiState.update { it.copy(code = code, errorMessage = null) }
        autoGeneratePassword()
    }

    fun toggleShowPassword() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    private fun autoGeneratePassword() {
        val state = _uiState.value
        val password = PasswordGenerator.generate(state.keyword, state.code)
        
        if (password != null) {
            _uiState.update { it.copy(generatedPassword = password, errorMessage = null) }
            
            // Always save keyword immediately
            if (state.keyword.isNotBlank()) {
                viewModelScope.launch {
                    preferencesManager.setSavedKeyword(state.keyword)
                }
            }
            
            // Debounce history save - only save after 5 seconds of no changes
            scheduleHistorySave(state.code)
        } else {
            _uiState.update { it.copy(generatedPassword = "") }
            // Cancel pending history save if password becomes invalid
            historySaveJob?.cancel()
        }
    }
    
    private fun scheduleHistorySave(code: String) {
        // Cancel any pending save
        historySaveJob?.cancel()
        
        if (code.isBlank()) return
        
        historySaveJob = viewModelScope.launch {
            delay(HISTORY_SAVE_DELAY_MS)
            
            // After delay, check if code is still the same and save
            val currentCode = _uiState.value.code
            if (currentCode == code && currentCode.isNotBlank()) {
                val existing = database.historyDao().findByCode(currentCode)
                if (existing != null) {
                    // Update timestamp
                    database.historyDao().insert(existing.copy(timestamp = System.currentTimeMillis()))
                } else {
                    database.historyDao().insert(HistoryItem(code = currentCode))
                }
            }
        }
    }

    fun selectHistoryItem(item: HistoryItem) {
        _uiState.update { it.copy(code = item.code) }
        // Don't schedule save for history items - they're already saved
        // Just generate password
        val state = _uiState.value
        val password = PasswordGenerator.generate(state.keyword, item.code)
        if (password != null) {
            _uiState.update { it.copy(generatedPassword = password, errorMessage = null) }
        }
    }

    fun deleteHistoryItem(item: HistoryItem) {
        viewModelScope.launch {
            database.historyDao().delete(item)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    class Factory(
        private val database: AppDatabase,
        private val preferencesManager: PreferencesManager
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(database, preferencesManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
