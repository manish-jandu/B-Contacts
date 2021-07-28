package com.manishjandu.bcontacts.ui.fragments.selectMultipleContact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.models.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectMultipleContactViewModel @Inject constructor(
    private val repo: ContactsRepository,
) : ViewModel() {

    private val _contacts=MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> =_contacts

    fun getBContacts()=viewModelScope.launch {
        val result=repo.getContactsFromSavedContact()
        val newContacts = arrayListOf<Contact>()

        for (item in result){
            newContacts.add(Contact(item.contactId,item.name,item.phone))
        }

        _contacts.postValue(newContacts)
    }

}