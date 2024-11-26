package io.hvk.bluechatapp.ui.people

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.hvk.bluechatapp.data.AppDatabase
import io.hvk.bluechatapp.data.User
import io.hvk.bluechatapp.bluetooth.BluetoothManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.Job

data class PeopleUiState(
    val users: List<User> = emptyList(),
    val isScanning: Boolean = false,
    val error: String? = null,
    val selectedUsers: Set<Long> = emptySet(),
    val isSelectionMode: Boolean = false,
    val needsPermissions: Boolean = false,
    val bluetoothDevices: List<BluetoothDevice> = emptyList()
)

class PeopleViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val bluetoothManager = BluetoothManager(application)
    private val _uiState = MutableStateFlow(PeopleUiState())
    val uiState: StateFlow<PeopleUiState> = _uiState.asStateFlow()
    private var scanJob: Job? = null

    init {
        viewModelScope.launch {
            userDao.getAllUsers().collect { users ->
                _uiState.value = _uiState.value.copy(
                    users = users,
                    needsPermissions = !bluetoothManager.hasRequiredPermissions()
                )
            }
        }
    }

    fun startScan() {
        if (!bluetoothManager.hasRequiredPermissions()) {
            _uiState.value = _uiState.value.copy(
                needsPermissions = true,
                error = "Bluetooth permissions required"
            )
            return
        }

        if (!bluetoothManager.isBluetoothEnabled()) {
            _uiState.value = _uiState.value.copy(
                error = "Bluetooth is not enabled"
            )
            return
        }

        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isScanning = true,
                    error = null,
                    bluetoothDevices = emptyList()
                )

                bluetoothManager.scanForDevices().collect { device ->
                    val currentDevices = _uiState.value.bluetoothDevices
                    if (!currentDevices.contains(device)) {
                        _uiState.value = _uiState.value.copy(
                            bluetoothDevices = currentDevices + device
                        )
                    }
                }
            } catch (e: SecurityException) {
                _uiState.value = _uiState.value.copy(
                    error = "Missing permissions: ${e.message}",
                    needsPermissions = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Scan failed: ${e.message}"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isScanning = false)
            }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        bluetoothManager.stopScanning()
        _uiState.value = _uiState.value.copy(isScanning = false)
    }

    fun toggleUserSelection(userId: Long) {
        val currentSelected = _uiState.value.selectedUsers
        val newSelected = if (currentSelected.contains(userId)) {
            currentSelected - userId
        } else {
            currentSelected + userId
        }
        
        _uiState.value = _uiState.value.copy(
            selectedUsers = newSelected,
            isSelectionMode = newSelected.isNotEmpty()
        )
    }

    fun exitSelectionMode() {
        _uiState.value = _uiState.value.copy(
            selectedUsers = emptySet(),
            isSelectionMode = false
        )
    }
} 