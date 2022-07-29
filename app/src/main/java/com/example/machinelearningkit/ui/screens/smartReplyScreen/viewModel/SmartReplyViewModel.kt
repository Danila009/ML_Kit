package com.example.machinelearningkit.ui.screens.smartReplyScreen.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.machinelearningkit.data.userDatabase.dao.ChatMessageDAO
import com.example.machinelearningkit.data.userDatabase.model.ChatMessage
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.TextMessage
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SmartReplyViewModel @Inject constructor(
    private val chatMessageDAO: ChatMessageDAO
) :ViewModel() {

    val chatMessages = chatMessageDAO.getChatMessages()

    fun insertChatMessage(item:ChatMessage){
        chatMessageDAO.insertChatMessage(item)
            .subscribeOn(Schedulers.io())
            .subscribe({},{})
    }

    fun deleteChatMessageByTimestampMillis(timestampMillis:Long){
        chatMessageDAO.deleteChatMessageByTimestampMillis(timestampMillis)
            .subscribeOn(Schedulers.io())
            .subscribe({},{})
    }

    fun suggestReplies(
        conversation:List<TextMessage>,
        onSuccess:(List<String>) -> Unit
    ) {
        val smartReply = SmartReply.getClient()

        smartReply.suggestReplies(conversation)
            .addOnSuccessListener{ result ->
                Log.e("ResponseResult", result.suggestions.map { it.text }.toString())
                onSuccess(result.suggestions.map { it.text })
            }
    }
}