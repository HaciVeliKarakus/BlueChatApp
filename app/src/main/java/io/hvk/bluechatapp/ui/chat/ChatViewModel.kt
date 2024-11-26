package io.hvk.bluechatapp.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.hvk.bluechatapp.data.AppDatabase
import io.hvk.bluechatapp.data.Chat
import io.hvk.bluechatapp.data.Message
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val chatDao = AppDatabase.getDatabase(application).chatDao()
    private val messageDao = AppDatabase.getDatabase(application).messageDao()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentChatId: Long? = null

    fun startChat(userId: Long) {
        viewModelScope.launch {
            val chat = Chat(
                userId = userId,
                lastMessage = "",
                timestamp = System.currentTimeMillis()
            )
            currentChatId = chatDao.insertChat(chat)
            loadMessages(currentChatId!!)
        }
    }

    private fun loadMessages(chatId: Long) {
        viewModelScope.launch {
            messageDao.getMessagesForChat(chatId).collect { messages ->
                _uiState.value = _uiState.value.copy(
                    messages = messages,
                    isLoading = false
                )
            }
        }
    }

    fun sendMessage(text: String, userId: Long) {
        viewModelScope.launch {
            val chatId = currentChatId ?: run {
                val chat = Chat(
                    userId = userId,
                    lastMessage = text,
                    timestamp = System.currentTimeMillis()
                )
                chatDao.insertChat(chat)
            }

            val message = Message(
                chatId = chatId,
                message = text,
                isFromMe = true
            )
            messageDao.insertMessage(message)

            // Update last message in chat
            chatDao.insertChat(
                Chat(
                    id = chatId,
                    userId = userId,
                    lastMessage = text,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
} 