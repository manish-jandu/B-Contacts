package com.manishjandu.bcontacts.ui.fragments.addEditBirthday

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.entities.Birthday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditBirthDayViewModel @Inject constructor(private val repo: ContactsRepository) : ViewModel() {
    private val _birthday=MutableLiveData<Birthday>()
    val birthday: LiveData<Birthday> =_birthday

    private val addEditBirthdayChannel = Channel<AddEditBirthdayEvent>()
    val addEditBirthdayEvent = addEditBirthdayChannel.receiveAsFlow()

    fun getBirthday(requestCode:Int)=viewModelScope.launch {
        val result = repo.getBirthday(requestCode)
        _birthday.postValue(result)
    }

    fun setFutureMessage(birthday: Birthday)=viewModelScope.launch{
        repo.addBirthday(birthday)
        if(birthday.isAutoSet){
            //set alarm
            addEditBirthdayChannel.send(AddEditBirthdayEvent.SetAlarm(birthday))
        }else{
            //cancel alarm
            addEditBirthdayChannel.send(AddEditBirthdayEvent.CancelAlarm(birthday))
        }
        addEditBirthdayChannel.send(AddEditBirthdayEvent.MessageAddingSuccessful)
    }

    sealed class AddEditBirthdayEvent{
        object MessageAddingSuccessful:AddEditBirthdayEvent()
        data class SetAlarm(val birthday: Birthday):AddEditBirthdayEvent()
        data class CancelAlarm(val birthday: Birthday):AddEditBirthdayEvent()
    }

}