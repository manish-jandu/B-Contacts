package com.manishjandu.bcontacts.ui.fragments.addEditMultipleUsers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.entities.Message
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
    private val _message=MutableLiveData<Message>()
    val message: LiveData<Message> =_message

    private val addEditMessageChannel=Channel<AddEditMessageEvent>()
    val addEditMessageEvent=addEditMessageChannel.receiveAsFlow()

    fun setFutureMessage(
        multipleUserMessageId: Int?=null,
        contacts: List<Contact>,
        message:String,
        minutes: Int,
        hour: Int,
        day: Int,
        month: Int,
        year: Int,
        timeInMillis: Long,
    ) = viewModelScope.launch {
          if(multipleUserMessageId == null){
              val newMessage = MultipleUserMessage(message = message,contacts = contacts,minutes = minutes,hour = hour,day = day,month = month,year = year
              ,timeInMillis = timeInMillis,)

              repo.addMultipleUserMessage(newMessage)
          }else{
              //edit user
          }
    }


    fun getMessage(messageId: Int)=viewModelScope.launch {

    }

    sealed class AddEditMessageEvent {
        object MessageAddingSuccessful : AddEditMessageEvent()
        data class SetAlarm(val message: Message) : AddEditMessageEvent()
        data class CancelAlarm(val messageId: Int) : AddEditMessageEvent()
    }

}