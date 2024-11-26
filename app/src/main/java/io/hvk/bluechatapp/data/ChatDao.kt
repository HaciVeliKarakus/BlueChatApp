package io.hvk.bluechatapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Transaction
    @Query("""
        SELECT c.*, u.name as userName, u.status as userStatus 
        FROM chats c 
        INNER JOIN users u ON c.userId = u.id 
        ORDER BY c.timestamp DESC
    """)
    fun getAllChatsWithUsers(): Flow<List<ChatWithUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<Chat>): List<Long>

    @Query("DELETE FROM chats")
    suspend fun deleteAllChats()
}

data class ChatWithUser(
    val id: Long,
    val userId: Long,
    val lastMessage: String,
    val timestamp: Long,
    val userName: String,
    val userStatus: String
) 