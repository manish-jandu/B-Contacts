package com.manishjandu.bcontacts.ui.fragments.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.entities.Notes
import com.manishjandu.bcontacts.data.local.entities.SavedContactWithNotes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val repo: ContactsRepository) :ViewModel() {
    private val _notes=MutableLiveData<SavedContactWithNotes>()
    val notes: LiveData<SavedContactWithNotes> = _notes

    private val notesEventChannel=Channel<NotesEvent>()
    val notesEvent=notesEventChannel.receiveAsFlow()

    fun getNotes(contactId: Long)=viewModelScope.launch {
        val result=repo.getNotes(contactId)
        _notes.postValue(result)
    }

    fun removeNote(note: Notes)=viewModelScope.launch {
        repo.removeNote(note)
        getNotes(note.contactId)
        notesEventChannel.send(NotesEvent.showUndoDeleteNoteMessage(note))
    }

    fun addDeletedNote(note: Notes)=viewModelScope.launch {
        repo.addNote(note)
        getNotes(note.contactId)
    }

    sealed class NotesEvent {
        data class showUndoDeleteNoteMessage(val note: Notes) : NotesEvent()
    }
}