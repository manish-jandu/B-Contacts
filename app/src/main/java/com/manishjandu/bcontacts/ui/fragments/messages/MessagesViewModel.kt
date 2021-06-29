package com.manishjandu.bcontacts.ui.fragments.messages


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.entities.Message
import com.manishjandu.bcontacts.data.local.entities.SavedContactWithMessages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(private val repo: ContactsRepository) : ViewModel() {
    private val _messages=MutableLiveData<SavedContactWithMessages>()
    val messages: LiveData<SavedContactWithMessages> =_messages

    private val messageEventChannel=Channel<MessageEvent>()
    val messageEvent=messageEventChannel.receiveAsFlow()

    fun getMessages(contactId: Long)=viewModelScope.launch {
        val result=repo.getMessages(contactId)
        _messages.postValue(result)
    }

    fun removeMessage(message: Message)=viewModelScope.launch {
        messageEventChannel.send(MessageEvent.CancelAlarm(message.messageId))
        repo.removeMessage(message)
        getMessages(message.contactId)
        messageEventChannel.send(MessageEvent.ShowUndoMessageDelete(message))
    }

    fun addDeletedMessage(message: Message)=viewModelScope.launch {
        repo.addMessage(message)
        getMessages(message.contactId)
    }


    sealed class MessageEvent {
        data class ShowUndoMessageDelete(val message: Message) : MessageEvent()
        data class CancelAlarm(val messageId: Int) : MessageEvent()
    }

}