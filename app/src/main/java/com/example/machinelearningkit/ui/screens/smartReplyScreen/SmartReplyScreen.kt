package com.example.machinelearningkit.ui.screens.smartReplyScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.machinelearningkit.data.userDatabase.model.ChatMessage
import com.example.machinelearningkit.ui.screens.smartReplyScreen.viewModel.SmartReplyViewModel
import com.google.mlkit.nl.smartreply.TextMessage
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@Composable
fun SmartReplyScreen(
    viewModel:SmartReplyViewModel
) {

    var message by rememberSaveable{ mutableStateOf("Hello") }
    var messageList by rememberSaveable{ mutableStateOf(listOf<TextMessage>()) }
    var messageSmartReply by rememberSaveable { mutableStateOf(listOf<String>()) }

    viewModel.chatMessages
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ items ->
           messageList = items.map { item ->
               TextMessage.createForLocalUser(item.messageText,item.timestampMillis)
           }
        },{})

    LaunchedEffect(key1 = messageList.lastIndex){
        if (messageList.isNotEmpty()){
            viewModel.suggestReplies(
                conversation = messageList
            ){ result -> messageSmartReply = result }
        }
    }

    LazyColumn {

        item {
            LazyRow {
                items(messageSmartReply){ item ->
                    TextButton(
                        modifier = Modifier.padding(10.dp),
                        onClick = { viewModel.insertChatMessage(
                            item = ChatMessage(messageText = item)
                        ) }
                    ) {
                        Text(
                            text = item,
                            color = Color.Magenta
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = message,
                onValueChange = { message = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        viewModel.insertChatMessage(
                            item = ChatMessage(messageText = message)
                        )
                        message = ""
                    }
                )
            )
        }

        items(messageList) { item ->
            TextButton(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    viewModel.deleteChatMessageByTimestampMillis(item.timestampMillis)
                }
            ) {
                Text(
                    text = item.messageText,
                    color = Color.Red
                )
            }
        }
    }
}