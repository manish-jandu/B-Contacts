package com.manishjandu.bcontacts.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmManagerUtil(val intent: Intent, val alarmManager: AlarmManager) {

    fun setAlarm(
        context: Context,
        timeInMillis: Long,
        requestCode: Int,
        message: String,
        contactNumber: String
    ) {
        intent.putExtra("message", message)
        intent.putExtra("contactNumber", contactNumber)
        val pendingIntent=PendingIntent.getBroadcast(context, requestCode, intent, 0)

        alarmManager.set(
            AlarmManager.RTC,
            timeInMillis,
            pendingIntent
        )
        Toast.makeText(context, "Message is set", Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context, requestCode: Int) {
        val pendingIntent=PendingIntent.getBroadcast(context, requestCode, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

}