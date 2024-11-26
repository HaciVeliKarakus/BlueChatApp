package io.hvk.bluechatapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.hvk.bluechatapp.ThemeDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val deviceName: String = "",
    val isBluetoothEnabled: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = ThemeDataStore(application)
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.isDarkTheme.collect { isDark ->
                _uiState.value = _uiState.value.copy(isDarkTheme = isDark)
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            dataStore.toggleTheme()
        }
    }

    fun updateDeviceName(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(deviceName = name)
            // TODO: Save device name to preferences
        }
    }

    fun toggleBluetooth() {
        // TODO: Implement Bluetooth toggle
    }
} 