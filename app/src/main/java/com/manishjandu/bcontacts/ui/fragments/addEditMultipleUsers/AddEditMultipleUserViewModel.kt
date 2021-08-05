package com.manishjandu.bcontacts.ui.fragments.addEditMultipleUsers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.entities.MultipleUserMessage
import com.manishjandu.bcontacts.data.models.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditMultipleUserViewModel @Inject constructor(private val repo: ContactsRepository) :
    ViewModel() {
    private val _message=MutableLiveData<MultipleUserMessage>()
    val message: LiveData<MultipleUserMessage> get()=_message

    private val addEditMessageChannel=Channel<AddEditMessageEvent>()
    val addEditMessageEvent=addEditMessageChannel.receiveAsFlow()

    fun setFutureMessage(
        multipleUserMessageId: Int?=null,
        contacts: List<Contact>,
        message: String,
        minutes: Int,
        hour: Int,
        day: Int,
        month: Int,
        year: Int,
        timeInMillis: Long,
    )=viewModelScope.launch {

        if (multipleUserMessageId == null) {
            val message=MultipleUserMessage(
                message=message,
                contacts=contacts,
                minutes=minutes,
                hour=hour,
                day=day,
                month=month,
                year=year,
                timeInMillis=timeInMillis,
            )
            val messagedId=repo.addMultipleUserMessage(message)
            message.multipleUserMessageId=messagedId
            addEditMessageChannel.send(AddEditMessageEvent.SetAlarm(message))
        } else {
            val message=MultipleUserMessage(
                message=message,
                contacts=contacts,
                minutes=minutes,
                hour=hour,
                day=day,
                month=month,
                year=year,
                timeInMillis=timeInMillis,
                multipleUserMessageId=multipleUserMessageId
            )
            repo.addMultipleUserMessage(message)
            addEditMessageChannel.send(AddEditMessageEvent.CancelAlarm(message.multipleUserMessageId))
            addEditMessageChannel.send(AddEditMessageEvent.SetAlarm(message))
        }
        addEditMessageChannel.send(AddEditMessageEvent.MessageAddingSuccessful)
    }


    fun getMessage(messageId: Int)=viewModelScope.launch {
        val result=repo.getMultipleUserMessage(messageId)
        _message.postValue(result)
    }

    sealed class AddEditMessageEvent {
        object MessageAddingSuccessful : AddEditMessageEvent()
        data class SetAlarm(val multipleUserMessage: MultipleUserMessage) : AddEditMessageEvent()
        data class CancelAlarm(val messageId: Int) : AddEditMessageEvent()
    }

}