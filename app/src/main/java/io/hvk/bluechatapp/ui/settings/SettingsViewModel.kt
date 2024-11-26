package io.hvk.bluechatapp.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.hvk.bluechatapp.ThemeDataStore
import io.hvk.bluechatapp.data.AppDatabase
import io.hvk.bluechatapp.data.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val deviceName: String = "",
    val isBluetoothEnabled: Boolean = false,
    val userName: String = "John Doe",
    val userStatus: String = "Available",
    val users: List<User> = emptyList(),
    val isGenerating: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = ThemeDataStore(application)
    private val userDao = AppDatabase.getDatabase(application).userDao()
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                dataStore.isDarkTheme,
                userDao.getAllUsers()
            ) { isDark, users ->
                _uiState.value.copy(
                    isDarkTheme = isDark,
                    users = users
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun generateRandomUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true)
            
            val firstNames = listOf("John", "Jane", "Mike", "Sarah", "David", "Emma", "James", "Lisa")
            val lastNames = listOf("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller")
            val statuses = listOf("Available", "Busy", "At work", "Sleeping", "In a meeting", "At the gym")
            
            val newUsers = (1..100).map {
                User(
                    name = "${firstNames.random()} ${lastNames.random()}",
                    status = statuses.random()
                )
            }
            
            userDao.insertUsers(newUsers)
            _uiState.value = _uiState.value.copy(isGenerating = false)
        }
    }

    fun clearUsers() {
        viewModelScope.launch {
            userDao.deleteAllUsers()
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

    fun updateUserName(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(userName = name)
            // TODO: Save user name to preferences
        }
    }

    fun updateUserStatus(status: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(userStatus = status)
            // TODO: Save user status to preferences
        }
    }

    fun toggleBluetooth() {
        // TODO: Implement Bluetooth toggle
    }
} 