package com.manishjandu.bcontacts.ui.fragments.multipleUsers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.entities.MultipleUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MultipleUsersViewModel @Inject constructor(private val repo: ContactsRepository) :
    ViewModel() {
    private val _messages=MutableLiveData<List<MultipleUserMessage>>()
    val messages: LiveData<List<MultipleUserMessage>> get()=_messages

    private val messageEventChannel=Channel<MessageEvent>()
    val messageEvent=messageEventChannel.receiveAsFlow()


    fun getMessages()=viewModelScope.launch {
        val result=repo.getAllMultipleUserMessages()
        _messages.postValue(result)
    }

    fun removeMessage(message: MultipleUserMessage)=viewModelScope.launch {
        messageEventChannel.send(MessageEvent.CancelAlarm(message.multipleUserMessageId))
        repo.removeMultipleUserMessage(message)
        getMessages()
        messageEventChannel.send(MessageEvent.ShowUndoMessageDelete(message))
    }

    fun addDeletedMessage(multipleUserMessage: MultipleUserMessage)=viewModelScope.launch {
        repo.addMultipleUserMessage(multipleUserMessage)
        getMessages()
    }

    sealed class MessageEvent {
        data class ShowUndoMessageDelete(val multipleUserMessage: MultipleUserMessage) : MessageEvent()
        data class CancelAlarm(val messageId: Int) : MessageEvent()
    }
}