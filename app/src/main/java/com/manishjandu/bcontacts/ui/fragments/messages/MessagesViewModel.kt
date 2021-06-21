package com.manishjandu.bcontacts.ui.fragments.messages

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.LocalDatabase
import com.manishjandu.bcontacts.data.local.entities.Message
import com.manishjandu.bcontacts.data.local.entities.SavedContactWithMessages
import com.manishjandu.bcontacts.utils.FutureMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MessagesViewModel(val app: Application) : AndroidViewModel(app) {
    private val _messages=MutableLiveData<SavedContactWithMessages>()
     val messages: LiveData<SavedContactWithMessages> =_messages

    private val alarmManager=
        app.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val intent=Intent(app.applicationContext, FutureMessage::class.java)

    private val contactDao=
        LocalDatabase.getSavedContactDatabase(app.applicationContext).contactDao()
    private val repo=ContactsRepository(contactDao)

    private val messageEventChannel = Channel<MessageEvent>()
    val messageEvent = messageEventChannel.receiveAsFlow()

    fun getMessages(contactId: Long)=viewModelScope.launch {
        val result=repo.getMessages(contactId)
        _messages.postValue(result)
    }

    fun removeMessage(message: Message) =viewModelScope.launch{
        repo.removeMessage(message)
        cancelAlarm(message.messageId)
        getMessages(message.contactId)
        messageEventChannel.send(MessageEvent.showUndoMessageDelete(message))
    }

    fun addDeletedMessage(message: Message) = viewModelScope.launch {
        setAlarm(message.timeInMillis,message.messageId,message.message,message.phone)
        repo.addMessage(message)
    }

    private fun setAlarm(
        timeInMillis: Long,
        requestCode: Int,
        message: String,
        contactNumber: String
    ) {
        intent.putExtra("message", message)
        intent.putExtra("contactNumber", contactNumber)
        val pendingIntent=PendingIntent.getBroadcast(app.applicationContext, requestCode, intent, 0)

        alarmManager.set(
            AlarmManager.RTC,
            timeInMillis,
            pendingIntent
        )
        Toast.makeText(app.applicationContext, "Alarm is set", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm(requestCode: Int) {
        val pendingIntent=PendingIntent.getBroadcast(app.applicationContext, requestCode, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    sealed class MessageEvent{
        data class showUndoMessageDelete(val message: Message) : MessageEvent()
    }

}