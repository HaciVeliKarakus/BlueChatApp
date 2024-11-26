package io.hvk.bluechatapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = ThemeDataStore(application)
    
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    init {
        viewModelScope.launch {
            dataStore.isDarkTheme.collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }
    }
    
    fun toggleTheme() {
        viewModelScope.launch {
            dataStore.toggleTheme()
        }
    }
} 