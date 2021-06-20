package com.manishjandu.bcontacts.ui.fragments.addEditMessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AddEditMessageViewModel : ViewModel() {

    fun setFutureMessage(
        minutes: Int, hour: Int, day: Int, month: Int, year: Int, timeInMills: Long,
        message: String, contactId: Long, contactNumber: String, messageId: Int
    )=viewModelScope.launch {
        if(messageId == 0){
            //Todo: new message
        }else{
            //Todo: edited Message
        }
    }


}