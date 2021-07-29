package com.manishjandu.bcontacts.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log

class FutureMessage : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val message=intent.getStringExtra("message")
        val contactNumber=intent.getStringExtra("contactNumber")

        Log.i(
            "FutureMessage",
            "onReceive: message is $message, contactNumber is $contactNumber "
        )
        if (message != null) {
            if (!contactNumber.isNullOrEmpty()) {
                    val numbers=contactNumber.split(",")
                    for (item in numbers) {
                        if(item.isNotEmpty()){
                            sendSms(item, message)
                        }
                    }
            }
        }

    }


    private fun sendSms(address: String, message: String) {
        val addressWithCountryCode="+91$address"
        SmsManager.getDefault().sendTextMessage(
            addressWithCountryCode, null,
            message, null, null
        )
    }
}