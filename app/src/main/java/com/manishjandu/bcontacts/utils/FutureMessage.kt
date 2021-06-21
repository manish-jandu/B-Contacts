package com.manishjandu.bcontacts.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FutureMessage : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
//        if (context != null) {
//            val contactDao=
//                LocalDatabase.getSavedContactDatabase(context).contactDao()
//            val repo=ContactsRepository(contactDao)
//
//            val messageId=intent.getStringExtra("messageId")!!.toInt()
//
//            runBlocking {
//                val message=repo.getMessage(messageId)
//                sendSms(message.phone, message.message)
//            }
//
//        }

    }

//    private fun sendSms(address: String, message: String) {
//        val addressWithCountryCode="+91$address"
//        SmsManager.getDefault().sendTextMessage(
//            addressWithCountryCode, null,
//            message, null, null
//        )
//    }
}