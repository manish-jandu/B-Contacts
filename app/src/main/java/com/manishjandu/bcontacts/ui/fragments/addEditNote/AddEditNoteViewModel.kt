package com.manishjandu.bcontacts.ui.fragments.addEditNote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.LocalDatabase
import com.manishjandu.bcontacts.data.local.entities.Notes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditNoteViewModel(app: Application) : AndroidViewModel(app) {
    private val contactDao=
        LocalDatabase.getSavedContactDatabase(app.applicationContext).contactDao()
    private val repo=ContactsRepository(contactDao)

    private val addEditNoteEventChannel = Channel<AddEditNoteEvent>()
    val addEditNoteEvent = addEditNoteEventChannel.receiveAsFlow()

    fun addNote(contactId: Long, title: String?, description: String?)=viewModelScope.launch {
        if (!title.isNullOrEmpty() && !description.isNullOrEmpty()) {
            val note=Notes(contactId, title, description)
            repo.addNote(note)
            addEditNoteEventChannel.send(AddEditNoteEvent.NoteAddingSuccessful)
        } else {
            addEditNoteEventChannel.send(AddEditNoteEvent.NoteAddingNotSuccessful)
        }
    }

    sealed class AddEditNoteEvent{
        object NoteAddingSuccessful : AddEditNoteEvent()
        object NoteAddingNotSuccessful : AddEditNoteEvent()
    }
}