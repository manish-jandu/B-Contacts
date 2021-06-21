package com.manishjandu.bcontacts.ui.fragments.messages

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.LocalDatabase
import com.manishjandu.bcontacts.data.local.entities.Message
import com.manishjandu.bcontacts.data.local.entities.SavedContactWithMessages
import com.manishjandu.bcontacts.utils.FutureMessage
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

    fun getMessages(contactId: Long)=viewModelScope.launch {
        val result=repo.getMessages(contactId)
        _messages.postValue(result)
    }

    fun removeNote(message: Message) =viewModelScope.launch{
        repo.removeMessage(message)
        cancelAlarm(message.messageId)
        getMessages(message.contactId)
    }

    private fun cancelAlarm(requestCode: Int) {
        val pendingIntent=PendingIntent.getBroadcast(app.applicationContext, requestCode, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

}