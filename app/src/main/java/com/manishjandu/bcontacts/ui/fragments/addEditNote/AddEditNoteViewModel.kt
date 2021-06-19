package com.manishjandu.bcontacts.ui.fragments.addEditNote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.LocalDatabase
import com.manishjandu.bcontacts.data.local.entities.Notes
import kotlinx.coroutines.launch

class AddEditNoteViewModel(app: Application) : AndroidViewModel(app) {
    private val contactDao=
        LocalDatabase.getSavedContactDatabase(app.applicationContext).contactDao()
    private val repo=ContactsRepository(contactDao)

    fun addNote(contactId: Long, title: String?, description: String?)=viewModelScope.launch {
        if (!title.isNullOrEmpty() && !description.isNullOrEmpty()) {
            val note=Notes(contactId, title, description)
            repo.addNote(note)
            //Todo: send success event and pop
        } else {
            //Todo:send event to show snackbar error
        }
    }
}