package io.hvk.bluechatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.hvk.bluechatapp.ui.theme.BlueChatAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlueChatAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        TabItem(
            title = "Chats",
            icon = R.drawable.ic_chat,
            screen = { ChatListScreen() }
        ),
        TabItem(
            title = "People",
            icon = R.drawable.ic_people,
            screen = { PersonListScreen() }
        ),
        TabItem(
            title = "Settings",
            icon = R.drawable.ic_settings,
            screen = { SettingsScreen() }
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(id = tab.icon), contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            tabs[selectedTab].screen()
        }
    }
}

data class TabItem(
    val title: String,
    val icon: Int,
    val screen: @Composable () -> Unit
)

@Composable
fun ChatListScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Chat List")
        // Add chat list implementation
    }
}

@Composable
fun PersonListScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Person List")
        // Add person list implementation
    }
}

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Settings")
        // Add settings implementation
    }
}