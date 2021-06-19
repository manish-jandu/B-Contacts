package com.manishjandu.bcontacts.ui.fragments.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.LocalDatabase
import com.manishjandu.bcontacts.data.local.entities.SavedContactWithNotes
import kotlinx.coroutines.launch

class NotesViewModel(app: Application) : AndroidViewModel(app) {
    private val _notes=MutableLiveData<SavedContactWithNotes>()
    val notes: LiveData<SavedContactWithNotes> = _notes


    private val contactDao=
        LocalDatabase.getSavedContactDatabase(app.applicationContext).contactDao()
    private val repo=ContactsRepository(contactDao)

    fun getNotes(contactId: Long)=viewModelScope.launch {
        val result=repo.getNotes(contactId)
        _notes.postValue(result)
    }

}