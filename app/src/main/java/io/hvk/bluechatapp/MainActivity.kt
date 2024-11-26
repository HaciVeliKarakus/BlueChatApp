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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Switch
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import io.hvk.bluechatapp.ui.chat.ChatViewModel
import io.hvk.bluechatapp.ui.people.PeopleViewModel
import io.hvk.bluechatapp.ui.settings.SettingsViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextAlign
import io.hvk.bluechatapp.data.User
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import io.hvk.bluechatapp.ui.people.PeopleUiState
import io.hvk.bluechatapp.ui.people.ProfileDetailScreen

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
                    containerColor = iOSBlue
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
                containerColor = iOSBlue,
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
                            indicatorColor = iOSLightBlue
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
    var showingProfile by remember { mutableStateOf<User?>(null) }

    if (showingProfile != null) {
        ProfileDetailScreen(
            user = showingProfile!!,
            onBackClick = { showingProfile = null },
            onStartChat = {
                // TODO: Implement chat start
                showingProfile = null
            }
        )
    } else {
        PersonListContent(
            uiState = uiState,
            onUserClick = { showingProfile = it },
            onUserLongClick = { viewModel.toggleUserSelection(it.id) },
            onScanClick = { if (uiState.isScanning) viewModel.stopScan() else viewModel.startScan() },
            onExitSelection = { viewModel.exitSelectionMode() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonListContent(
    uiState: PeopleUiState,
    onUserClick: (User) -> Unit,
    onUserLongClick: (User) -> Unit,
    onScanClick: () -> Unit,
    onExitSelection: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isSelectionMode) {
            TopAppBar(
                title = { Text("${uiState.selectedUsers.size} selected") },
                navigationIcon = {
                    IconButton(onClick = onExitSelection) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Exit selection"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = iOSBlue
                )
            )
        }

        // Scan Button
        Button(
            onClick = { 
//                if (uiState.isScanning) viewModel.stopScan() else viewModel.startScan()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = iOSBlue
            )
        ) {
            Text(if (uiState.isScanning) "Stop Scanning" else "Start Scanning")
        }

        if (uiState.isScanning) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = iOSBlue
            )
        }

        if (uiState.users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No users found\nGenerate some users in Settings tab",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.users) { user ->
                    UserListItem(
                        user = user,
                        isSelected = uiState.selectedUsers.contains(user.id),
                        onClick = { onUserClick(user) },
                        onLongClick = { onUserLongClick(user) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserListItem(
    user: User,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Avatar
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = iOSLightBlue
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = user.name.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // User Info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = user.status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // Connection Status or Action Button
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bluetooth_connect),
                contentDescription = "Connect",
                tint = iOSBlue
            )
        }
    }

    Divider(
        modifier = Modifier.padding(start = 80.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
    )
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // User Profile Section
        UserProfileItem(
            userName = uiState.userName,
            userStatus = uiState.userStatus,
            onEditClick = { /* TODO: Show edit profile dialog */ }
        )
        
        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
        )

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

        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
        )

        // Random Users Section
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Random Users",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.generateRandomUsers() },
                    enabled = !uiState.isGenerating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = iOSBlue
                    )
                ) {
                    if (uiState.isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Generate 100 Users")
                    }
                }
                
                Button(
                    onClick = { viewModel.clearUsers() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Clear Users")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Total Users: ${uiState.users.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun UserProfileItem(
    userName: String,
    userStatus: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Avatar
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = iOSLightBlue
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
        
        // User Info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = userStatus,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        
        // Edit Button
        IconButton(onClick = onEditClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = "Edit profile",
                tint = iOSBlue
            )
        }
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