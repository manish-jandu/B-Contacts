package com.manishjandu.bcontacts.ui.fragments.addEditMessage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.entities.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditMessageViewModel @Inject constructor(private val repo: ContactsRepository) : ViewModel() {
    private val _message=MutableLiveData<Message>()
    val message: LiveData<Message> =_message

    private val addEditMessageChannel=Channel<AddEditMessageEvent>()
    val addEditMessageEvent=addEditMessageChannel.receiveAsFlow()

    fun setFutureMessage(message: Message)=viewModelScope.launch {
        if (message.messageId == 0) {
            var newMessage=Message(
                message.minutes,
                message.hour,
                message.day,
                message.month,
                message.year,
                message.timeInMillis,
                message.message,
                message.phone,
                message.contactId
            )
            val newMessageId=repo.addMessage(newMessage)
            newMessage.messageId=newMessageId
            addEditMessageChannel.send(AddEditMessageEvent.SetAlarm(newMessage))
        } else {
            repo.addMessage(message)
            addEditMessageChannel.send(AddEditMessageEvent.CancelAlarm(message.messageId))
            addEditMessageChannel.send(AddEditMessageEvent.SetAlarm(message))
        }
        addEditMessageChannel.send(AddEditMessageEvent.MessageAddingSuccessful)
    }


    fun getMessage(messageId: Int)=viewModelScope.launch {
        val result=repo.getMessage(messageId)
        _message.postValue(result)
    }

    sealed class AddEditMessageEvent {
        object MessageAddingSuccessful : AddEditMessageEvent()
        data class SetAlarm(val message: Message) : AddEditMessageEvent()
        data class CancelAlarm(val messageId: Int) : AddEditMessageEvent()
    }
}