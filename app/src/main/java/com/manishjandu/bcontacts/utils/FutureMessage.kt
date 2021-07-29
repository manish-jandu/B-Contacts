package com.manishjandu.bcontacts.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager

class FutureMessage : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val message=intent.getStringExtra("message")!!
        val contactNumber=intent.getStringExtra("contactNumber")!!

        sendSms(contactNumber, message)
    }

    private fun sendSms(address: String, message: String) {
        val addressWithCountryCode="+91$address"
        SmsManager.getDefault().sendTextMessage(
            addressWithCountryCode, null,
            message, null, null
        )
    }
}