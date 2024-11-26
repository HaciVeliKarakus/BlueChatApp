package io.hvk.bluechatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.hvk.bluechatapp.ui.theme.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.Switch
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import io.hvk.bluechatapp.ui.chat.ChatViewModel
import io.hvk.bluechatapp.ui.people.PeopleViewModel
import io.hvk.bluechatapp.ui.settings.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkTheme = viewModel.isDarkTheme.collectAsState()
            
            BlueChatAppTheme(darkTheme = isDarkTheme.value) {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        TabItem(
            title = "CHATS",
            icon = R.drawable.ic_chat,
            screen = { ChatListScreen() }
        ),
        TabItem(
            title = "PEOPLE",
            icon = R.drawable.ic_people,
            screen = { PersonListScreen() }
        ),
        TabItem(
            title = "SETTINGS",
            icon = R.drawable.ic_settings,
            screen = { SettingsScreen() }
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BlueChatApp",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhatsAppGreen
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Search action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { /* TODO: More options */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more),
                            contentDescription = "More",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = WhatsAppGreen,
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = tab.icon),
                                contentDescription = tab.title,
                                tint = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.6f)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.6f)
                            )
                        },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.6f),
                            indicatorColor = WhatsAppLightGreen
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            tabs[selectedTab].screen()
        }
    }
}

@Composable
fun ChatListScreen(viewModel: ChatViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.messages.isEmpty()) {
            EmptyChatsView()
        } else {
            // TODO: Implement chat list
        }
    }
}

@Composable
fun EmptyChatsView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No chats yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start a conversation by connecting with nearby devices",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

data class TabItem(
    val title: String,
    val icon: Int,
    val screen: @Composable () -> Unit
)

@Composable
fun PersonListScreen(viewModel: PeopleViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isScanning) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        
        if (uiState.devices.isEmpty()) {
            Text(
                text = "No devices found",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            // TODO: Implement device list
        }
    }
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Theme Toggle
        SettingsItem(
            title = "Dark Theme",
            subtitle = "Toggle dark/light theme",
            trailing = {
                Switch(
                    checked = uiState.isDarkTheme,
                    onCheckedChange = { viewModel.toggleTheme() }
                )
            }
        )
        
        // Device Name
        SettingsItem(
            title = "Device Name",
            subtitle = uiState.deviceName,
            trailing = {
                IconButton(onClick = { /* TODO: Show edit dialog */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit name"
                    )
                }
            }
        )
        
        // Bluetooth Toggle
        SettingsItem(
            title = "Bluetooth",
            subtitle = if (uiState.isBluetoothEnabled) "On" else "Off",
            trailing = {
                Switch(
                    checked = uiState.isBluetoothEnabled,
                    onCheckedChange = { viewModel.toggleBluetooth() }
                )
            }
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        trailing()
    }
}