package com.manishjandu.bcontacts.ui.fragments.addEditMessage

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
import com.manishjandu.bcontacts.utils.FutureMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditMessageViewModel(val app: Application) : AndroidViewModel(app) {
    private val _message = MutableLiveData<Message>()
    val message:LiveData<Message> = _message

    private val contactDao=
        LocalDatabase.getSavedContactDatabase(app.applicationContext).contactDao()
    private val repo=ContactsRepository(contactDao)

    private val alarmManager=
        app.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val intent=Intent(app.applicationContext, FutureMessage::class.java)

    private val addEditMessageChannel = Channel<AddEditMessageEvent>()
    val addEditMessageEvent = addEditMessageChannel.receiveAsFlow()

    fun setFutureMessage(
        minutes: Int, hour: Int, day: Int, month: Int, year: Int, timeInMillis: Long,
        message: String, contactId: Long, contactNumber: String, messageId: Int
    )=viewModelScope.launch {
        if (messageId == 0) {
            val newMessage=Message(
                minutes, hour, day, month, year, timeInMillis, message, contactNumber, contactId
            )
            val newMessageId=repo.addMessage(newMessage)
            setAlarm(timeInMillis, newMessageId, message, contactNumber)
        } else {
            val oldMessage=Message(
                minutes,
                hour,
                day,
                month,
                year,
                timeInMillis,
                message,
                contactNumber,
                contactId,
                messageId
            )
            repo.addMessage(oldMessage)
            cancelAlarm(messageId)
            setAlarm(timeInMillis, messageId, message, contactNumber)
        }
        addEditMessageChannel.send(AddEditMessageEvent.MessageAddingSuccessful)
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

    fun getMessage(messageId: Int) =viewModelScope.launch{
        val result = repo.getMessage(messageId)
        _message.postValue(result)
    }

    sealed class AddEditMessageEvent {
        object MessageAddingSuccessful : AddEditMessageEvent()
    }
}