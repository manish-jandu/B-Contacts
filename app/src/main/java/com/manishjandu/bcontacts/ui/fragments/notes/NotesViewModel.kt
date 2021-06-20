package com.manishjandu.bcontacts.ui.fragments.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.LocalDatabase
import com.manishjandu.bcontacts.data.local.entities.Notes
import com.manishjandu.bcontacts.data.local.entities.SavedContactWithNotes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NotesViewModel(app: Application) : AndroidViewModel(app) {
    private val _notes=MutableLiveData<SavedContactWithNotes>()
    val notes: LiveData<SavedContactWithNotes> = _notes

    private val notesEventChannel=Channel<NotesEvent>()
    val notesEvent=notesEventChannel.receiveAsFlow()

    private val contactDao=
        LocalDatabase.getSavedContactDatabase(app.applicationContext).contactDao()
    private val repo=ContactsRepository(contactDao)

    fun getNotes(contactId: Long)=viewModelScope.launch {
        val result=repo.getNotes(contactId)
        _notes.postValue(result)
    }

    fun removeNote(note: Notes)=viewModelScope.launch {
        repo.removeNote(note)
        getNotes(note.contactId)
        notesEventChannel.send(NotesEvent.showUndoDeleteNoteMessage(note))
    }

    fun addNote(note: Notes)=viewModelScope.launch {
        repo.addNote(note)
        getNotes(note.contactId)
    }

    sealed class NotesEvent {
        data class showUndoDeleteNoteMessage(val note: Notes) : NotesEvent()
    }
}