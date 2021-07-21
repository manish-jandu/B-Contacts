package com.manishjandu.bcontacts.ui.fragments.bcontactBottomSheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.entities.Message
import com.manishjandu.bcontacts.data.local.entities.SavedContact
import com.manishjandu.bcontacts.data.models.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BContactBottomSheetViewModel @Inject constructor(
    private val repo: ContactsRepository,
) : ViewModel() {

    private val _futureMessages=MutableLiveData<List<Message>>()
    val futureMessage: LiveData<List<Message>> = _futureMessages

    private val bottomSheetEventChannel = Channel<BottomSheetEvent>()
    val bottomSheetEvent = bottomSheetEventChannel.receiveAsFlow()

    fun removeContactLocally(contact: Contact)=viewModelScope.launch {
        val savedContact = SavedContact(contact.contactId,contact.name,contact.phone)
        removeAlarms(savedContact)
        repo.removeContactFromSavedContact(savedContact)
        bottomSheetEventChannel.send(BottomSheetEvent.BContactRemoved)
    }

    private suspend fun removeAlarms(savedContact: SavedContact) {
        val savedMessages=repo.getMessages(savedContact.contactId)
        _futureMessages.postValue(savedMessages.messages)
    }

    sealed class BottomSheetEvent{
        object BContactRemoved:BottomSheetEvent()
    }
}