package com.manishjandu.bcontacts.ui.fragments.addEditMessage

import android.app.Application
import android.telephony.SmsManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AddEditMessageViewModel(val app:Application) : AndroidViewModel(app) {

    fun setFutureMessage(
        minutes: Int, hour: Int, day: Int, month: Int, year: Int, timeInMillis: Long,
        message: String, contactId: Long, contactNumber: String, messageId: Int
    )=viewModelScope.launch {
//        if(messageId == 0){
//            val alarmManager = getSystemService(app.applicationContext.ALARM_SERVICE) as AlarmManager
//            val intent = Intent(app.applicationContext, FutureMessage::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(app.applicationContext, 0, intent, 0)
//            alarmManager.setRepeating(
//                AlarmManager.RTC,
//                timeInMillis,
//                AlarmManager.INTERVAL_DAY,
//                pendingIntent
//            )
//            Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show()
//        }else{
//            //Todo: edited Message
//        }

    }

    private fun sendSms(){
        SmsManager.getDefault().sendTextMessage("addresswith+91", null,
            "this is trial message", null, null);
    }

}