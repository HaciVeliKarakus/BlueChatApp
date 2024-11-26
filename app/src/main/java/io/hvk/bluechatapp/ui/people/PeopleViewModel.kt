package io.hvk.bluechatapp.ui.people

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BluetoothDevice(
    val name: String,
    val address: String,
    val isConnected: Boolean = false
)

data class PeopleUiState(
    val devices: List<BluetoothDevice> = emptyList(),
    val isScanning: Boolean = false,
    val error: String? = null
)

class PeopleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PeopleUiState())
    val uiState: StateFlow<PeopleUiState> = _uiState.asStateFlow()

    fun startScan() {
        // TODO: Implement Bluetooth scanning
    }

    fun stopScan() {
        // TODO: Implement scan stop
    }

    fun connectToDevice(device: BluetoothDevice) {
        // TODO: Implement device connection
    }
} 